package graph

/**
 * データフロー関係を意味するエッジを表すクラス
 */
class DependenceEdge(val from: VarNode, val to: VarNode) {
    companion object {
        fun onlyDef(def: VarNode): DependenceEdge {
            return DependenceEdge(VarNode.dummy(), def)
        }

        fun onlyUse(use: VarNode): DependenceEdge {
            return DependenceEdge(use, VarNode.dummy())
        }

        fun fromString(s: String): DependenceEdge {
            val regex = Regex("(.*)->(.*)")
            val m = regex.matchEntire(s) ?: throw Error("フォーマットに沿ってないで: $s")
            val (_, from, to) = m.groupValues
            return DependenceEdge(VarNode(from.trim()), VarNode(to.trim()))
        }
    }

    override fun toString(): String {
        return when {
            from.name.isEmpty() && to.name.isEmpty() -> throw Error("from も to も空やで")
            from.name.isEmpty() -> "-> ${this.to.name}"
            to.name.isEmpty() -> return "${this.from.name} ->"
            else -> return "${this.from.name} -> ${this.to.name}"
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DependenceEdge) {
            this.from == other.from && this.to == other.to
        } else {
            false
        }
    }
}
