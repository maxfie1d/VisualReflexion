import file.ConfigLoader
import file.SliceOutputLoader
import models.Project
import org.junit.Test
import java.io.File

class ControlDependenceTest {
    @Test
    fun test1() {
        val project = Project(
                SliceOutputLoader(File("src/test/resources/issue42.c.slice.json")).load()!!,
                ConfigLoader(File("src/test/resources/control_dependence/1_config.json")).load()!!
        )

        testDependenceEdges(
                "src/test/resources/control_dependence/1_expected.json",
                project
        )
    }

    @Test
    fun test2() {
        val project = Project(
                SliceOutputLoader(File("src/test/resources/issue42.c.slice.json")).load()!!,
                ConfigLoader(File("src/test/resources/control_dependence/2_config.json")).load()!!
        )

        testDependenceEdges(
                "src/test/resources/control_dependence/2_expected.json",
                project
        )
    }

    @Test
    fun test3() {
        val project = Project(
                SliceOutputLoader(File("src/test/resources/issue42.c.slice.json")).load()!!,
                ConfigLoader(File("src/test/resources/control_dependence/3_config.json")).load()!!
        )

        testDependenceEdges(
                "src/test/resources/control_dependence/3_expected.json",
                project
        )
    }
}
