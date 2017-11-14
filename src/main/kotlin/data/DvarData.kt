package data

import java.text.ParseException

class DvarData private constructor(
        private val varialbeName: String,
        private val varialbeId: String) {
    companion object {
        fun fromString(s: String): DvarData {
            val regex = Regex("(.+)\\((.+)\\)")
            val m = regex.matchEntire(s) ?: throw ParseException(s, 0)
            val variableName = m.groups[0]!!.value
            val variableId = m.groups[1]!!.value
            return DvarData(variableName, variableId)
        }
    }

    override fun toString(): String {
        return "${this.varialbeName}(${this.varialbeId})"
    }
}
