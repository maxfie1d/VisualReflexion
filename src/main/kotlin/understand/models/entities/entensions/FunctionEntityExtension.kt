package understand.models.entities.entensions

import data.FunctionData
import understand.models.entities.FunctionEntity

fun FunctionEntity.toFunctionData(): FunctionData {
    val id = this.id()
    val funcName = this.name()
    val filePath = this.getDeclaredFile().name()
    val range = this.getDeclaredRange()

    return FunctionData(
            id,
            funcName,
            "user-defined",
            filePath,
            range
    )
}
