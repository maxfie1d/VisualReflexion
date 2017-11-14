package data

import java.text.ParseException

class ProgramRange(
        private val startLine: Int,
        private val endLine: Int) {
    companion object {
        fun fromString(s: String): ProgramRange {
            val regex = Regex("(\\d+)-(\\d+)")
            val m = regex.matchEntire(s) ?: throw ParseException(s, 0)
            val (s_startLine, s_endLine) = m.groupValues.drop(1)
            val startLine = s_startLine.toInt()
            val endLine = s_endLine.toInt()
            return ProgramRange(startLine, endLine)
        }
    }

    override fun toString(): String {
        return "${this.startLine}-${this.endLine}"
    }

    fun toIntRange(): IntRange {
        return IntRange(this.startLine, this.endLine)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ProgramRange) {
            this.startLine == other.startLine
                    && this.endLine == other.endLine
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = startLine
        result = 31 * result + endLine
        return result
    }
}

class FunctionData constructor(
        /**
         * 関数ID
         */
        val id: String,

        /**
         * 関数名
         */
        val funcName: String,

        /**
         * 関数の種類
         * (例: user-defined のみ)
         */
        val kind: String,

        /**
         * 関数の定義されたファイルパス
         */
        val filePath: String,

        /**
         * 関数の宣言範囲(行番号は1はじまり)
         * (例: 18-41)
         */
        val declareRange: ProgramRange
) {
    companion object {
        fun createFromCsvRow(csv_row: String): FunctionData {
            val split = csv_row.split("\t")
            if (split.size != 5) {
                throw Error("不正なアイテム数です: ${csv_row}")
            } else {
                val (s_id, s_funcName, s_kind, s_filePath, s_declareRange) = split
                return FunctionData(
                        s_id,
                        s_funcName,
                        s_kind,
                        s_filePath,
                        ProgramRange.fromString(s_declareRange)
                )
            }
        }
    }

    override fun toString(): String {
        return listOf(
                this.id,
                this.funcName,
                this.kind,
                this.filePath,
                this.declareRange
        ).joinToString(", ") { it.toString() }
    }
}
