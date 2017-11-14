package understand.models.entities

import com.scitools.understand.Entity
import data.DefUseData
import data.ProgramPoint
import data.VariableData

class VariableEntity(
        _entity: Entity
) : EntityBase(_entity) {

    /**
     * グローバル変数ならばtrue、そうでなければfalse
     */
    fun isGlobalVariable(): Boolean {
        return this.kind() == "Global Object"
    }

    /**
     * 構造体変数ならばtrue, そうでなければfalse
     */
    fun isStructVariable(): Boolean {
        // TODO: この実装は正確ではない
        val primitives = arrayOf(
                "int", "float", "double", "char"
        )

        val enums = arrayOf(
                "colorid_t"
        )

        val merge = primitives.plus(enums)

        return getVariableType().split(Regex("\\s+")).find {
            merge.contains(it)
        } == null
    }

    fun getDeclaredFile(): FileEntity {
        return if (this.isGlobalVariable()) {
            val ref = this.refs("Definein", "File").first()
            FileEntity(ref.ent())
        } else {
            this.getDeclaredFunction()!!.getDeclaredFile()
        }
    }

    fun getDeclaredFunction(): FunctionEntity? {
        return if (this.isGlobalVariable()) {
            null
        } else {
            val ref = this.refs("", "Function").first()
            FunctionEntity(ref.ent())
        }
    }

    fun getVariableType(): String {
        // Understandの情報ブラウザーに表示される
        // 各種情報を取得できる
        // 例:
        // Global Object g_actuator_output
        //  Defined in: app.c
        //  Type: ActuatorOutput
        //  References
        //    Use run_actuator  actuator.c(7)
        //    Define app.c  app.c(9)
        //    Set initialize  app.c(14)
        //    Set control  control.c(26)
        //    Declare global.h  global.h(7)

        val informationBrowserData = this._entity.ib("")

        val typeInfoLine = informationBrowserData.find { it.startsWith("  Type:") }!!
        val regex = Regex("^  Type: (.+)$")
        val result = regex.find(typeInfoLine)!!
        return result.groups[1]!!.value
    }

    private fun memberName(
            predicate: (VariableData) -> List<DefUseData>,
            structMemberVars: List<VariableData>,
            pp: ProgramPoint
    ): List<String> {
        return if (!this.isStructVariable()) listOf("") else {
            // 構造体変数の場合は、メンバ変数のdef/useが同じ行で起きているものをマッチングする
            val matchedVar = structMemberVars.filter {
                // プログラムポイントが一致するものを探す
                predicate(it).find {
                    it.programPoint == pp
                } != null
            }

            if (matchedVar.count() == 0) {
                listOf("")
            } else {
                matchedVar.map { it.name }
            }
        }
    }

    private fun copyDefUse(func: FunctionEntity,
                           memberName: String,
                           predicate: (VariableData) -> List<DefUseData>,
                           structMemberVars: List<VariableData>
    ): List<DefUseData> {
        return func.calledFunctionRefs().map { ref ->
            val locationFunc = FunctionEntity(ref.ent())
            val location = ProgramPoint(
                    ref.line(),
                    locationFunc.id()
            )

            val d = DefUseData(
                    location,
                    memberName,
                    func.id()
            )

            // 再帰的にコピーする
            val moreCopies = copyDefUse(
                    locationFunc,
                    memberName,
                    predicate,
                    structMemberVars
            )

            val ret = listOf(
                    listOf(d),
                    moreCopies
            ).flatten()
            ret
        }.flatten()
    }

    private fun derivedFroms(
            // 例: Setby, Definein, Useby
            refKindString: List<String>,
            structMemberVars: List<VariableData>,
            // 例: { a: VariableData -> a.def }
            predicate: (VariableData) -> List<DefUseData>
    ): List<DefUseData> {
        return if (this.isGlobalVariable()) {
            // その変数がDefされた関数をすべて取得する
            val refs = refKindString.flatMap { this.refs(it, "Function", false).toList() }
            refs.map { ref ->
                val func = FunctionEntity(ref.ent())
                func.calledProgramPoints().map { funcCallPP ->
                    val pp2 = ProgramPoint(
                            ref.line(),
                            func.id()
                    )
                    val memberNames = this.memberName(predicate, structMemberVars, pp2)

                    memberNames.flatMap { memberName ->
                        // def/useを再帰的にコピーして伝播させている
                        copyDefUse(func, memberName, predicate, structMemberVars)
                    }
                }.flatten()
            }.flatten()
        } else emptyList()
    }

    private fun normalDefUses(
            refKindString: List<String>,
            structMemberVars: List<VariableData>,
            predicate: (VariableData) -> List<DefUseData>
    ): List<DefUseData> {
        val refs = refKindString.flatMap { this.refs(it, null).toList() }
        return refs.flatMap {
            val refFuncEntity = it.ent()
            val pp = ProgramPoint(it.line(), refFuncEntity.id().toString())

            val m = this.memberName(predicate, structMemberVars, pp)

            m.map { memberName ->
                DefUseData(pp, memberName, "")
            }
        }
    }

    private fun defUses(
            refKindString: List<String>,
            structMemberVars: List<VariableData>,
            predicate: (VariableData) -> List<DefUseData>
    ): List<DefUseData> {
        val derivedFroms = this.derivedFroms(refKindString, structMemberVars, predicate)
        val normals = this.normalDefUses(refKindString, structMemberVars, predicate)
        return listOf(normals, derivedFroms).flatten().sortedBy { it.programPoint.lineNumber }
    }

    fun getDefs(structMemberVars: List<VariableData>): List<DefUseData> {
        val predicate = { a: VariableData -> a.def }
        return this.defUses(listOf("Setby", "Definein"), structMemberVars, predicate)
    }

    fun getUses(structMemberVars: List<VariableData>): List<DefUseData> {
        val predicate = { a: VariableData -> a.use }
        return this.defUses(listOf("Useby"), structMemberVars, predicate)
    }
}
