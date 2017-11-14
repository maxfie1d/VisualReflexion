import graph.DependenceEdge
import graph.GraphModel
import graph.GraphPath
import org.junit.Assert.assertEquals
import org.junit.Test

class TestGraphModel {

    // TODO: テストを増やす

    @Test
    fun test1() {
        val l = listOf(
                "c -> b",
                "a -> b",
                "a -> c",
                "c -> d"
        )

        t1(l, "a", "d", "a -> c -> d")
        t2(l, "d", "a -> c -> d")
    }

    @Test
    fun test2() {
        val l = listOf(
                "a -> b",
                "b -> c",
                "b -> d",
                "d -> e",
                "c -> f",
                "e -> f",
                "f -> g"
        )

        t1(l, "a", "g", "a -> b -> d -> e -> f -> g")
        t2(l, "g", "a -> b -> d -> e -> f -> g")
    }

    private fun t1(edges: List<String>, startNodeName: String, endNodeName: String, expectedPath: String) {
        val gm = GraphModel(
                edges.map(DependenceEdge.Companion::fromString)
        )
        val actual = gm.solvePath(startNodeName, endNodeName)?.get(0)
        val expected = GraphPath.fromString(expectedPath)
        assertEquals(expected, actual)
    }

    private fun t2(edges: List<String>, endNodeName: String, expectedPath: String) {
        val gm = GraphModel(
                edges.map(DependenceEdge.Companion::fromString)
        )
        val actual = gm.solveLongestPathToGoal(endNodeName)?.get(0)
        val expected = GraphPath.fromString(expectedPath)
        assertEquals(expected, actual)
    }
}
