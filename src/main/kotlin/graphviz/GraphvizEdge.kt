package graphviz

class GraphvizEdge(
        private val from: String,
        private val to: String,
        private val label: GraphvizLabel? = null
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("${this.from} -> ${this.to} ${this.label?.toString()}")
        return sb.toString()
    }
}
