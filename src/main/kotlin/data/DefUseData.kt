package data

import java.text.ParseException

class DefUseData(
        /**
         * PP(関数IDと行番号で表現)
         */
        val programPoint: ProgramPoint,

        /**
         * 変数が構造体の場合、def/useのあったメンバ名が入る
         */
        val memberName: String,

        /**
         * 関数から伝播してdef/useが発生した場合、その関数のIDが入る
         */
        val derivedFromFuncId: String) {
    companion object {
        fun fromString(s: String): DefUseData {
            val pp = ProgramPoint.fromString(s)
            val memberName: String = if (s.indexOf("(") >= 0) {
                val regex = Regex("\\((.+)\\)")
                val m = regex.find(s)
                if (m == null) {
                    throw ParseException(s, 0)
                } else {
                    m.groups[1]!!.value
                }
            } else ""

            val derivedFromFuncId = if (s.indexOf("<") >= 0) {
                val regex = Regex("<from_(.+)>")
                val m = regex.find(s)
                if (m == null) {
                    throw ParseException(s, 0)
                } else {
                    m.groups[1]!!.value
                }
            } else ""

            return DefUseData(
                    pp, memberName, derivedFromFuncId
            )
        }
    }

    override fun toString(): String {
        val a = if (this.memberName.isEmpty())
            ""
        else "(${this.memberName})"
        val b = if (this.derivedFromFuncId.isEmpty())
            ""
        else "<from_${this.derivedFromFuncId}>"

        return this.programPoint.toString() + a + b;
    }

    /**
     * 他の関数由来で起きたdef/useならばtrue, そうでなければfalse
     */
    fun isDerived(): Boolean {
        return !this.derivedFromFuncId.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DefUseData) {
            this.programPoint == other.programPoint
                    && this.memberName == other.memberName
                    && this.derivedFromFuncId == other.derivedFromFuncId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = programPoint.hashCode()
        result = 31 * result + memberName.hashCode()
        result = 31 * result + derivedFromFuncId.hashCode()
        return result
    }
}
