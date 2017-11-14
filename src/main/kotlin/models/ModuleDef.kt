package models

import data.FunctionData

/**
 * モジュールの定義
 * とりあえず関数とモジュールを一対一マッピングとする
 */
class ModuleDef(
        val name: String,
        val map_to: FunctionData
) {

    override fun toString(): String {
        return this.appeal(this.name)
    }

    private fun appeal(s: String): String {
        val sb = StringBuilder()
        sb.appendln("#".repeat(s.length + 4))
        sb.appendln("# $s #")
        sb.append("#".repeat(s.length + 4))
        return sb.toString()
    }
}
