package graph

/**
 * 存在するエッジを保持しておいて、
 * 出発ノードから目的ノードへ到達するような
 * 経路を計算したりするクラス
 */
class GraphModel {
    private val shrinkedEdges: Set<DependenceEdge>

    constructor(edges: List<DependenceEdge>) {
        this.shrinkedEdges = shrink(edges)
    }

    /**
     * 出発ノードから目標ノードへの経路を解く
     * 存在しなければnullを返す
     */
    fun solvePath(startNodeName: String, goalNodeName: String): List<GraphPath>? {
        return search(setOf(VarNodeWithBackwordLink(startNodeName, null)),
                VarNode(goalNodeName),
                emptySet())
    }

    /**
     * ゴールにたどり着くような経路で最長のものを探す
     */
    fun solveLongestPathToGoal(goalNodeName: String): List<GraphPath>? {
        // すべてのノードを出発地点として、計算する?

        val a = this.shrinkedEdges.map {
            it.from.name
        }.mapNotNull {
            this.solvePath(it, goalNodeName)
        }.flatten().distinctBy {
            // 終了が同じものは除去
            it.endNode().name
        }

        return if (a.isEmpty()) {
            null
        } else {
            a.groupBy { it.endNode() }.mapNotNull {
                it.value.maxBy { it.length() }
            }
        }
    }

    private fun shrink(l: List<DependenceEdge>): Set<DependenceEdge> {
        val r = LinkedHashMap<String, DependenceEdge>()
        // キャンセルされるものはカット
        for (x in l) {
            r.put(x.to.name, x)
        }
        return r.map { it.value }.toSet()
    }

    private fun search(startNodes: Set<VarNodeWithBackwordLink>, goalNode: VarNode, consumedNodes: Set<VarNodeWithBackwordLink>): List<GraphPath>? {
        val x = startNodes.map { a ->
            // そのノードが始点であるエッジのうち
            this.shrinkedEdges.filter {
                it.from.name == a.name
            }.filter {
                // まだ到達していないノードがあれば
                !consumedNodes.contains(it.to)
            }.map { VarNodeWithBackwordLink(it.to.name, a) }
        }.flatten()

        val goal = x.filter {
            // 完全一致
            val a = it.name == goalNode.name
            // メンバ名を含めなければ一致
            val b = it.name.substringBefore(".") == goalNode.name
            a || b
        }

        return when {
            !goal.isEmpty() ->
                // 終了ノードにたどり着いたので探索終了
                goal.map { buildGraphPath(it) }
            x.isEmpty() ->
                // 探索失敗、終了
                null
            else -> search(x.toSet(), goalNode, consumedNodes.plus(x))
        }
    }

    private fun buildGraphPath(n: VarNodeWithBackwordLink): GraphPath {
        // 辿ってきたノードをつなぎ合わせて経路を作る
        fun makelist(x: VarNodeWithBackwordLink): List<VarNode> {
            return if (x.previousNode == null) {
                listOf(x)
            } else {
                makelist(x.previousNode).plus(x)
            }
        }

        return GraphPath(makelist(n))
    }
}


/**
 * VarNodeに前のノードの情報を加えたもの
 */
private class VarNodeWithBackwordLink(name: String, val previousNode: VarNodeWithBackwordLink? = null) : VarNode(name)
