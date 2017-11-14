package models

import graph.DependenceEdge
import graph.GraphModel
import graph.GraphPath
import graph.VarNode

/**
 * モジュール単位で起こったdef/useの流れを保持するクラス
 */
class ModuleUnit(
        val module: ModuleDef,
        val edges: List<DependenceEdge>,
        val usedVars: Set<VarNode>
) {

    private val gm: GraphModel

    init {
        this.gm = GraphModel(this.edges)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendln("@${this.module.name}")
        sb.append(this.edges.joinToString("\n") {
            it.toString()
        })
        return sb.toString()
    }

    // 目標変数に到達するようなdefルートを列挙する
    fun findPathTo(names: List<String>): List<GraphPath> {
        return names.mapNotNull {
            this.gm.solveLongestPathToGoal(it)
        }.flatten()
    }
}
