package data

import java.text.ParseException

class CfuncData private constructor(
        private val calledFunctionName: String,
        private val calledFunctionId: String,
        private val argIndex: Int,
        private val location: ProgramPoint
) {
    companion object {
        fun fromString(s: String): CfuncData {
            // e.g. validate(65a13216){1(value)}:23@93a9a72b
            val regex = Regex("(.+)\\((.+)\\)\\{(\\d+)(\\(.+\\))?}:(.+)")
            val m = regex.matchEntire(s) ?: throw ParseException(s, 0)
            val (calledFunctionName,
                    calledFunctionId,
                    s_argIndex,
                    _,
                    s_location) = m.groupValues.drop(1)

            val argIndex = s_argIndex.toInt()
            val location = ProgramPoint.fromString(s_location)
            return CfuncData(calledFunctionName, calledFunctionId, argIndex, location)
        }
    }

    override fun toString(): String {
        return "${this.calledFunctionName}(${this.calledFunctionId}){${this.argIndex}}:${this.location.toString()}"
    }
}
