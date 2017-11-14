import models.Project
import org.junit.Test
import java.io.File

data class ExpectedModuleDataflowJsonObject(
        val mod: String,
        val edges: List<String>
)

class SimpleRobotTestPerModule {
    @Test
    fun test() {
        val project = Project.createFromFile(
                File("src/test/resources/simple-robot/slice.json"),
                File("src/test/resources/simple-robot/1_config.json")
        )

        testDependenceEdges(
                "src/test/resources/simple-robot/per_module_expected.json",
                project
        )
    }
}
