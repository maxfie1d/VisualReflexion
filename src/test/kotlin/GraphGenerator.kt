import file.ConfigLoader
import file.SliceOutputLoader
import file.UnderstandLoader
import graphviz.GraphvizModel
import models.Project
import java.io.File

class GraphGenerator {
    fun generateFromSrcSliceOutput(
            sliceOutputPath: String,
            configPath: String
    ): GraphvizModel {

        val project = Project(
                SliceOutputLoader(File(sliceOutputPath)).load()!!,
                ConfigLoader(File(configPath)).load()!!
        )
        val result = project.analyzeAll()
        println(result.toString())

        return result.toGraphvizModel("G")
    }

    fun generateFromUnderstandProject(
            understandProjectPath: String,
            configPath: String
    ): GraphvizModel {
        val project = Project(
                UnderstandLoader(File(understandProjectPath)).load()!!,
                ConfigLoader(File(configPath)).load()!!
        )
        val result = project.analyzeAll()
        println(result.toString())

        return result.toGraphvizModel("G")
    }
}
