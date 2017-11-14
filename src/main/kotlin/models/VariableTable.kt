package models

import data.DefUseData
import data.VariableData
import java.io.File

data class VarAndDefUseDataTuple(
        val varData: VariableData,
        val defuseData: DefUseData
)

class VariableTable(
        private val vars: List<VariableData>
) : Iterable<VariableData> {
    override fun iterator(): Iterator<VariableData> {
        return this.vars.iterator()
    }

    private fun findVariable(findFrom: String, funcId: String, lineNumber: Int): List<VarAndDefUseDataTuple> {
        return this.vars.map { v ->
            when (findFrom) {
                "def" -> v.def
                "use" -> v.use
                else -> throw  Error()
            }.filter {
                !v.type.startsWith("const") &&
                        it.programPoint.funcId == funcId && it.programPoint.lineNumber == lineNumber
            }.map {
                VarAndDefUseDataTuple(v, it)
            }
        }.flatten()
    }

    fun findDefinedVariable(funcId: String, lineNumber: Int): List<VarAndDefUseDataTuple> {
        return this.findVariable("def", funcId, lineNumber)
    }

    fun findUsedVariable(funcId: String, lineNumber: Int): List<VarAndDefUseDataTuple> {
        return this.findVariable("use", funcId, lineNumber)
    }

    fun findByNameAndTypeAndFile(name: String, type: String, file: String): VariableData? {
        // 以下のようにsrcsliceとunderstandで表記違いがあるので
        // 変数名と型名を足してソートしたものを比較することで吸収している
        // srcsliceでは   name: lcd_str_buf[100], type: char
        // understandでは name: lcd_str_buf, type: char [100]
        // srcsliceでこのような表記になっているのは、srcsliceがトークンを前から順に読んでいくためなので
        // visualreflexionに取り込む段階で、understandのような表記に統一するのが最適と思われる

        val b = (name + type.replace("const", "")).replace(" ", "").toCharArray()
        b.sort()
        val bs = String(b)
        val r = this.filter {
            val a = (it.name + it.type.replace("const", "")).replace(" ", "").toCharArray()
            a.sort()
            String(a) == bs
        }

        return when {
            r.count() == 0 -> null
            r.count() == 1 -> r[0]
            else -> {
                val rr = r.find { File(it.file).nameWithoutExtension == File(file).nameWithoutExtension }
                rr
            }
        }
    }

    override fun toString(): String {
        return this.vars.joinToString("\n") {
            it.toString()
        }
    }
}
