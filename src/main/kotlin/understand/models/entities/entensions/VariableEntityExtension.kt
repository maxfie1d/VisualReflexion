package understand.models.entities.entensions

import data.VariableData
import understand.models.entities.VariableEntity

fun VariableEntity.toVariableData(structMemberVars: List<VariableData>): VariableData {
    val id = this.id()
    val file = this.getDeclaredFile().name()
    val func = this.getDeclaredFunction()?.name() ?: "__GLOBAL__"
    val type = this.getVariableType()
    val name = this.name()

    val def = this.getDefs(structMemberVars).distinct()
    val use = this.getUses(structMemberVars).distinct()

    return VariableData(
            id,
            file,
            func,
            type,
            name,
            def,
            use,
            emptyList(),
            emptyList()
    )
}
