import com.google.gson.Gson
import graph.DependenceEdge
import models.Project
import org.junit.Assert.assertEquals
import java.io.File

fun testDependenceEdges(expectedJsonPath: String, project: Project) {
    val expected = Gson().fromJson(File(expectedJsonPath).readText(),
            Array<ExpectedModuleDataflowJsonObject>::class.java)!!

    for ((mod, edges) in expected) {
        assertEquals(
                edges.map { DependenceEdge.fromString(it) }.toList(),
                project.analyzeAboutModule(mod)
        )
    }
}
