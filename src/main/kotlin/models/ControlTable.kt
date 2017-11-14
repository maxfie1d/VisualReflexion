package models

import java.text.ParseException

class ControlRange(
        val startLine: Int,
        val elseLine: Int,
        val endLine: Int
) {
    companion object {
        fun fromString(s: String): ControlRange {
            val split = s.split("..").map { it.toInt() }
            return if (split.size < 2 || split.size > 3) {
                throw ParseException(s, 0)
            } else {
                if (split.size == 2) {
                    startEnd(split[0], split[1])
                } else {
                    startElseEnd(split[0], split[1], split[2])
                }
            }
        }

        fun startEnd(startLine: Int, endLine: Int): ControlRange {
            return ControlRange(startLine, 0, endLine)
        }

        fun startElseEnd(startLine: Int, elseLine: Int, endLine: Int): ControlRange {
            return ControlRange(startLine, elseLine, endLine)
        }
    }

    override fun toString(): String {
        return if (this.elseLine == 0) {
            "${this.startLine}..${this.endLine}"
        } else {
            "${this.startLine}..${this.elseLine}..${this.endLine}"
        }
    }

    /**
     * if文の条件式が真の場合に実行される行番号の範囲を返します
     */
    fun alphaRange(): IntRange {
        val (a, b) = if (this.elseLine == 0) {
            Pair(this.startLine, this.endLine)
        } else {
            Pair(this.startLine, this.elseLine)
        }

        val (aa, bb) = Pair(a + 1, b - 1)
        if (aa > bb) {
            throw Error("範囲が不正です: $aa..$bb")
        } else {
            return IntRange(aa, bb)
        }
    }
}

class ControlData(
        val id: String,
        val filePath: String,
        val controlRange: ControlRange,
        val vars: Set<String>
) {
    companion object {
        fun createFromCsvRow(csvRow: String): ControlData {
            val split = csvRow.split("\t")
            return if (split.size != 4) {
                throw Error("不正なアイテム数です: $csvRow")
            } else {
                val (s_id, s_filePath, s_controlRange, s_vars) = split
                ControlData(
                        s_id,
                        s_filePath,
                        ControlRange.fromString(s_controlRange),
                        s_vars.split(",").toSet()
                )
            }
        }
    }
}

class ControlTable(
        private val controls: List<ControlData>
) {
    fun find(filePath: String, startLine: Int): ControlData? {
        return this.controls.find {
            it.filePath == filePath
                    && it.controlRange.startLine == startLine
        }
    }
}
