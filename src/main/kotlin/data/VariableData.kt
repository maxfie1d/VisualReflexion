package data

import graph.VarNode

class VariableData constructor(
        /**
         * 変数を一意に識別するためのID
         */
        val id: String,

        /**
         * 変数が宣言されたソースァイルパス(と行番号)
         * (例: src/control.c:7)
         */
        val file: String,

        /**
         * 変数が宣言された関数名。グローバル変数の場合は__GLOBAL__。
         * (例: run_actuator)
         */
        val func: String,

        /**
         * 変数の型名
         * (例: int)
         */
        val type: String,

        /**
         * 変数名
         * (例: correct_value)
         */
        val name: String,

        /**
         * 定義箇所
         *
         */
        val def: List<DefUseData>,
        val use: List<DefUseData>,
        val dvars: List<DvarData>,
        val cfuncs: List<CfuncData>
) {
    companion object {
        fun createFromCsvRow(csv_row: String): VariableData? {
            val split = csv_row.split("\t")
            if (split.size != 8) {
                throw Error("不正なアイテム数です: $csv_row")
            } else {
                val (s_id, s_file, s_func, s_var, s_def) = split
                val s_use = split[5]
                val s_dvars = split[6]
                val s_cfuncs = split[7]

                val regex = Regex("(.+) (.+)")
                val m = regex.matchEntire(s_var) ?: return null
                val (variableType, variableName) = m.groupValues.drop(1)
                return VariableData(
                        s_id,
                        s_file,
                        s_func,
                        variableType,
                        variableName,
                        if (s_def.isEmpty()) emptyList() else s_def.split(",").map { DefUseData.fromString(it) },
                        if (s_use.isEmpty()) emptyList() else s_use.split(",").map { DefUseData.fromString(it) },
                        if (s_dvars.isEmpty()) emptyList() else s_dvars.split(",").map { DvarData.fromString(it) },
                        if (s_cfuncs.isEmpty()) emptyList() else s_cfuncs.split(",").map { CfuncData.fromString(it) }
                )
            }
        }
    }

    override fun toString(): String {
        return listOf(
                this.id,
                this.file,
                this.func,
                "${this.type} ${this.name}",
                this.def,
                this.use
//                this.dvars,
//                this.cfuncs
        ).joinToString("\t") { it.toString() }
    }

    fun createVarNode(memberName: String? = null): VarNode {
        return VarNode(
                this.name + if (memberName.isNullOrEmpty()) "" else ".${memberName}")
    }
}
