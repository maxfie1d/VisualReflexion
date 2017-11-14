package graph

/**
 * 伝播するデータフローなどを表すグラフ経路のクラス
 */
class GraphPath(private val _nodes: List<VarNode>) {

    companion object {
        fun fromString(s: String): GraphPath {
            return GraphPath(s.split(Regex("\\s*->\\s*"))
                    .map { VarNode(it) }
                    .toList())
        }
    }

    override fun toString(): String {
        return this._nodes.joinToString(" -> ") { it.name }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is GraphPath) {
            if (this.length() == other.length()) {
                this._nodes.zip(other._nodes)
                        .map { (a, b) -> a == b }
                        .reduce { a, b -> a || b }
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * 経路長を返します
     */
    fun length(): Int {
        return this._nodes.size - 1
    }

    fun startNode(): VarNode {
        return this._nodes.first()
    }

    fun endNode(): VarNode {
        return this._nodes.last()
    }
}
