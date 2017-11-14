package file

import models.ControlTable
import understand.models.UnderstandProject
import java.io.File

class UnderstandLoader(
        private val understandProjectFile: File
) : LoaderBase {
    override fun load(): SliceOutput? {
        val understandProject = UnderstandProject(this.understandProjectFile)

        val funcTabel = understandProject.getFunctionTable()
        val varTable = understandProject.getVariableTable(funcTabel)

        return SliceOutput(
                varTable,
                funcTabel,
                ControlTable(emptyList())
        )
    }
}
