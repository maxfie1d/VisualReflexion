package understand

import understand.models.UnderstandProject
import understand.models.extensions.stringify
import java.io.File

fun main(args: Array<String>) {
    val projectPath = "C:/Users/n-isida/Documents/understand-test-sources/simple-c-program.udb"
    val project = UnderstandProject(File(projectPath))

    // mainfile中に存在する変数を取り出す

    val mainFunction = project.getMainFile().getMainFunction()

    mainFunction.getAllVariables().forEach {
        it.refs("Useby" +
                "", "", true).forEach {
            println(it.stringify())
        }

//        println(it.models.entities.entensions.models.extensions.stringify())
    }

//mainFunction.refs().forEach {
//    println(it.models.entities.entensions.models.extensions.stringify())
//}

    // それらの変数の参照を取得すれば
    // def/useの情報を取れる
    project.dispose()
}
