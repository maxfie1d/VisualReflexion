package understand.scripts

import understand.models.DirResolver
import understand.models.UnderstandProject
import java.io.File

/**
 * 変数テーブルを作る
 */
fun createVarTable() {
    val resolver = DirResolver()
    val projectPath = resolver.resolveRelativePath("udb/simple-robot.udb")
    val project = UnderstandProject(File(projectPath))

    println("<Function Table>")
    val funcTable = project.getFunctionTable()
    println(funcTable)
    println("==========================")

    println("<Variable Table>")
    val varTable = project.getVariableTable(funcTable)
    println(varTable)
}

fun main(args: Array<String>) {
    createVarTable()
}
