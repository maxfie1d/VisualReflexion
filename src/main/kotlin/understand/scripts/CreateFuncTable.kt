package understand.scripts

import understand.models.DirResolver
import understand.models.UnderstandProject
import java.io.File

/**
 * 関数テーブルを作る
 */
fun createFuncTable() {
    val resolver = DirResolver()
    val projectPath = resolver.resolveRelativePath("udb/simple-robot.udb")
    val project = UnderstandProject(File(projectPath))

    val funcTable = project.getFunctionTable()
    println(funcTable)
}

fun main(args: Array<String>) {
    createFuncTable()
}
