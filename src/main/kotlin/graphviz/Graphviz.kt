package graphviz

// TODO: このライブラリが使えるはず
// https://github.com/nidi3/graphviz-java
// ちゃんとMavenにも登録されている
// このライブラリをラッピングする感じでよかろう

enum class GraphvizNodeStyle(val value: String) {
    RECT("rect"),
    CIRCLE("circle")
}


class GraphvizModel(
        private val graphName: String,
        private val nodeStyle: GraphvizNodeStyle = GraphvizNodeStyle.RECT,
        private val edges: List<GraphvizEdge>
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendln("digraph ${this.graphName} {")
        sb.appendlnIndented("node [ shape = ${this.nodeStyle.value} ]", 4)
        this.edges.forEach { sb.appendlnIndented(it.toString(), 4) }
        sb.append("}")
        return sb.toString()
    }

    private fun StringBuilder.appendlnIndented(s: String, indent: Int) {
        this.appendln(" ".repeat(indent) + s)
    }
}
