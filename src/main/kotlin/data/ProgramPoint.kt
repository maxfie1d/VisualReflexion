package data

class ProgramPoint(
        val lineNumber: Int,
        val funcId: String) {
    companion object {
        fun fromString(s: String): ProgramPoint {
            val regex = Regex("^(\\d+)@(([a-z0-9]{8})|(__GLOBAL__))")
            val m = regex.find(s)
            if (m == null) {
                throw NullPointerException()
            } else {
                val line = m.groups[1]!!.value.toInt()
                val funcId = m.groups[2]!!.value

                return ProgramPoint(line, funcId)
            }
        }
    }

    override fun toString(): String {
        return "L${this.lineNumber}@${this.funcId}"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ProgramPoint) {
            this.lineNumber == other.lineNumber && this.funcId == other.funcId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = lineNumber
        result = 31 * result + funcId.hashCode()
        return result
    }
}
