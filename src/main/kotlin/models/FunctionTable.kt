package models

import data.FunctionData

class FunctionTable(
        private val funcs: List<FunctionData>
) : Iterable<FunctionData> {
    override fun iterator(): Iterator<FunctionData> {
        return this.funcs.iterator()
    }

    /**
     * 名前で関数を探します
     */
    fun findByName(name: String): FunctionData? {
        return this.funcs.find { it.funcName == name }
    }

    /**
     * IDで関数を探します
     */
    fun findById(id: String): FunctionData? {
        return this.funcs.find { it.id == id }
    }

    override fun toString(): String {
        return this.funcs.joinToString("\n") { it.toString() }
    }
}
