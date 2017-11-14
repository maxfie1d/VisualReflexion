package graphviz

enum class GraphvizLabelStyle {
    SOLID,
    DOTTED
}

class GraphvizLabel(
        private val label: String,
        private val style: GraphvizLabelStyle = GraphvizLabelStyle.SOLID
) {
    override fun toString(): String {
        val map = LinkedHashMap<String, String>()
        map.put("label", "\"${this.label}\"")
        if (this.style == GraphvizLabelStyle.DOTTED) {
            map.put("style", "dotted")
        }
        map.put("fontsize", "8.0")

        val a = map.toList().joinToString(", ") { (a, b) ->
            "$a = $b"
        }
        return "[ $a ]"
    }
}
