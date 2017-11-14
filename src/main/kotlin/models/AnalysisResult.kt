package models

import graph.VarNode
import graphviz.*

class DefData(
        val moduleName: String,
        val varNode: VarNode
) {
    override fun equals(other: Any?): Boolean {
        return if (other is DefData) {
            this.moduleName == other.moduleName
                    && this.varNode == other.varNode
        } else {
            false
        }
    }
}

class AnalysisResult(
        val moduleUnits: List<ModuleUnit>,
        private val targetVariables: List<String>
) {
    fun toGraphvizModel(graphName: String): GraphvizModel {
        val deflist = mutableListOf<DefData>()
        val edges = mutableListOf<GraphvizEdge>()
        // 1つ目のモジュールからのdefをリストに追加する
        val first = this.moduleUnits.first()
        val firstPaths = first.findPathTo(targetVariables)
        firstPaths.map {
            it.endNode()
        }.forEach {
            deflist.add(DefData(first.module.name, it))
        }

        for (moduleUnit in this.moduleUnits.drop(1)) {
            val paths = moduleUnit.findPathTo(targetVariables)
            val startNodeNames = paths.map { it.startNode() }
            for (def in deflist) {
                val style = if (startNodeNames.contains(def.varNode)
                        || moduleUnit.usedVars.contains(def.varNode)) {
                    GraphvizLabelStyle.SOLID
                } else {
                    GraphvizLabelStyle.DOTTED
                }

                val edge = GraphvizEdge(
                        def.moduleName,
                        moduleUnit.module.name,
                        GraphvizLabel(def.varNode.name, style)
                )
                edges.add(edge)
            }

            val additionalDefList = moduleUnit.findPathTo(targetVariables).map {
                DefData(moduleUnit.module.name, it.endNode())
            }

            // 再定義されるものは古い定義を削除する (古い定義は寿命が切れるため)
            deflist.removeIf { def ->
                additionalDefList.find {
                    it.varNode == def.varNode
                } != null
            }
            deflist.addAll(additionalDefList)
        }

        return GraphvizModel(graphName, GraphvizNodeStyle.RECT, edges)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        this.moduleUnits.forEach { mu ->
            sb.appendln(mu.module.toString());
            mu.findPathTo(this.targetVariables)
                    .forEach { sb.appendln(it) }
        }
        return sb.toString()
    }
}
