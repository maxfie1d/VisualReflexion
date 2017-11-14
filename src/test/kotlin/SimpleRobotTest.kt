import file.ConfigLoader
import file.SliceOutputLoader
import models.Project
import org.junit.Test
import understand.models.DirResolver
import java.io.File

class SimpleRobotTest {

    private val graphGenerator = GraphGenerator()

    @Test
    fun test1() {
        val graph = this.graphGenerator.generateFromSrcSliceOutput(
                "src/test/resources/simple-robot/slice.json",
                "src/test/resources/simple-robot/1_config.json"
        )

        println(graph.toString())
    }

    @Test
    fun test1Understand() {
        val understandProjectPath = DirResolver().resolveRelativePath("src/test/resources/udb/simple-robot.udb")
        val graph = this.graphGenerator.generateFromUnderstandProject(
                understandProjectPath,
                "src/test/resources/simple-robot/1_config.json"
        )

        println(graph)
    }

    @Test
    fun test2() {
        val project = Project(
                SliceOutputLoader(File("src/test/resources/simple-robot/slice.json")).load()!!,
                ConfigLoader(File("src/test/resources/simple-robot/2_config.json")).load()!!
        )

        val result = project.analyzeAll()
        println(result.toString())

        println(result.toGraphvizModel("G").toString())
    }
}
