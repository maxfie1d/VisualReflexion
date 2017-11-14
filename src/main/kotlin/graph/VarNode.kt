package graph

/**
 * 変数のデータフローを表すグラフの
 * ノード、すなわち変数を表すクラス
 */
open class VarNode(
        val name: String
) {
    override fun equals(other: Any?): Boolean {
        return if (other is VarNode) {
            this.name == other.name
        } else false
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }

    companion object {
        fun dummy(): VarNode {
            return VarNode("")
        }
    }

    override fun toString(): String {
        return this.name
    }
}
