package models

import data.FunctionData
import file.ConfigLoader
import file.SliceOutput
import file.SliceOutputLoader
import file.UnderstandLoader
import graph.DependenceEdge
import graph.VarNode
import java.io.File

class Project(
        private val tables: SliceOutput,
        private val config: AnalysisConfig
) {
    private val moduleDefs: List<ModuleDef>
    private val entryPointFunc: FunctionData

    val varTable: VariableTable
        get() = this.tables.variableTable

    val funcTable: FunctionTable
        get() = this.tables.functionTable

    val controlTable: ControlTable
        get() = this.tables.controlTable

    private val targetVars: List<String>
        get() = this.config.targetVariables

    val name: String
        get() = this.config.projectName

    val controlDataList: List<ConditionData>
        get() = this.config.controls ?: emptyList()

    init {
        this.moduleDefs = config.moduleMappings
                .map {
                    val funcData = this.funcTable.findByName(it.map_to)
                            ?: throw Error("存在しない関数です: ${it.map_to}")
                    ModuleDef(it.name, funcData)
                }
        // エントリポイントとなる関数を関数表から見つける
        this.entryPointFunc =
                this.funcTable.findByName(this.config.entryPointFuncName)
                ?: throw Error("エントリポイントとなる関数が見つかりませんでした")
    }

    companion object {
        fun createFromFile(sliceOutputFile: File, configFile: File): Project {
            val sliceOutput = when (sliceOutputFile.extension) {
                "json" -> SliceOutputLoader(sliceOutputFile)
                "udb" -> UnderstandLoader(sliceOutputFile)
                else -> null
            }?.load() ?: throw  Exception("読み込みに失敗しました: ${sliceOutputFile.absolutePath}")

            val config = ConfigLoader(configFile).load()
                    ?: throw  Exception("解析設定の読み込みに失敗しました")
            return Project(sliceOutput, config)
        }
    }

    /**
     * ソースコード１行文を解析する
     * 関数呼び出しなら、その関数の中をさらに探索して返す
     * 関数呼び出しでないなら、def/useの依存関係を調べて返す
     */
    private fun analyzeLine(defList: List<VarAndDefUseDataTuple>,
                            useList: List<VarAndDefUseDataTuple>,
                            currentModule: ModuleDef?, focusedModules: List<String>)
            : Pair<AnalysisResult?, Pair<List<DependenceEdge>, List<VarNode>>> {
        val def = defList.firstOrNull()
        val use = useList.firstOrNull()
        val aa = def?.defuseData ?: use?.defuseData

        fun useEdgeAndNode(): Pair<List<DependenceEdge>, List<VarNode>> {
            val edgesAndNodes = useList.map {
                val isTarget = this.targetVars.contains(it.varData.name)
                if (isTarget) {
                    val node = it.varData.createVarNode(it.defuseData.memberName)
                    val edge = DependenceEdge.onlyUse(node)
                    Pair(edge, node)
                } else {
                    Pair(null, null)
                }
            }

            val es = edgesAndNodes.mapNotNull { it.first }
            val ns = edgesAndNodes.mapNotNull { it.second }
            return Pair(es, ns)
        }

        return if (aa != null && aa.isDerived()) {
            val r = traverseFunc(aa.derivedFromFuncId, currentModule, focusedModules)
            Pair(r, useEdgeAndNode())
        }
        // 代入が起きた場合
        else if (def != null && use != null) {
            val isTarget = this.targetVars.contains(def.varData.name) || this.targetVars.contains(use.varData.name)
            if (isTarget) {
                val defNode = def.varData.createVarNode(def.defuseData.memberName)
                val useNode = use.varData.createVarNode(use.defuseData.memberName)
                val edge = DependenceEdge(useNode, defNode)
                Pair(null, Pair(listOf(edge), listOf(useNode)))
            } else {
                Pair(null, Pair(emptyList(), emptyList()))
            }
        }
        // def のみが起きた場合
        else if (def != null) {
            val isTarget = this.targetVars.contains(def.varData.name)
            if (isTarget) {
                val edge = DependenceEdge.onlyDef(def.varData.createVarNode(def.defuseData.memberName))
                Pair(null, Pair(listOf(edge), emptyList()))
            } else {
                Pair(null, Pair(emptyList(), emptyList()))
            }
        }
        // use のみが起きた場合
        else if (!useList.isEmpty()) {
            Pair(null, useEdgeAndNode())
        } else {
            Pair(null, Pair(emptyList(), emptyList()))
        }
    }

    private fun traverseFunc(
            // 探索するID
            functionId: String,

            // 現在探索中の関数が属するモジュール(あれば)
            currentModule: ModuleDef?,

            // 解析対象のモジュール(空ならばすべて対象とする)
            focusedModules: List<String> = emptyList()
    ): AnalysisResult {
        val mod = currentModule ?: this.moduleDefs.find { it.map_to.id == functionId }
        val shouldTraverse = functionId == this.entryPointFunc.id
                || focusedModules.isEmpty()
                || (mod != null && focusedModules.contains(mod.name))
        val result = if (shouldTraverse) {
            val funcData = this.funcTable.findById(functionId)
                    ?: throw Error("関数が見つかりません: $functionId")

            // スキップする行番号を集積する
            val skippedLines = this.controlDataList.filter {
                it.filePath == funcData.filePath
                        && it.isValueFalse
            }.mapNotNull {
                this.controlTable.find(it.filePath, it.lineNumber)
            }.map {
                it.controlRange.alphaRange().toList()
            }.flatten()

            val linesAnalysisResult = funcData.declareRange.toIntRange()
                    // 実行されない行番号はスキップする
                    .subtract(skippedLines)
                    .map { lineNumber ->
                        val (def, use) = Pair(this.varTable.findDefinedVariable(funcData.id, lineNumber),
                                this.varTable.findUsedVariable(funcData.id, lineNumber))
                        analyzeLine(def, use, mod, focusedModules)
                    }

            // モジュールユニット、依存辺、使用された変数を
            // flatten する
            val moduleUnits = linesAnalysisResult.mapNotNull { it.first?.moduleUnits }.flatten()
            val edges = linesAnalysisResult.map { it.second.first }.flatten()
            val usedVars = linesAnalysisResult.map { it.second.second }.flatten().toSet()

            if (mod != null) {
                val mu = ModuleUnit(mod, edges, usedVars)
                moduleUnits.plus(mu)
            } else {
                moduleUnits
            }.groupBy { it.module }.map { (moduleDef, moduleUnits) ->
                // 同一モジュールのものはひとまとめにする
                ModuleUnit(moduleDef,
                        moduleUnits.flatMap { it.edges },
                        moduleUnits.flatMap { it.usedVars }.toSet()
                )
            }
        } else emptyList()

        return AnalysisResult(result, this.targetVars)
    }

    fun analyzeAll(): AnalysisResult {
        // ここでmain関数を走査し始めるだけでいいはず
        return traverseFunc(this.entryPointFunc.id, null)
    }

    /**
     * 特定のモジュールのみを解析します
     */
    fun analyzeAboutModule(moduleName: String): List<DependenceEdge> {
        return traverseFunc(this.entryPointFunc.id, null, listOf(moduleName))
                .moduleUnits.first().edges
    }
}
