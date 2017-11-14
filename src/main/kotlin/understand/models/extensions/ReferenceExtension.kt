package understand.models.extensions

import com.scitools.understand.Reference
import understand.models.entities.entensions.stringify

fun Reference.stringify(): String {
    val where = "L${this.line()}, C${this.column()} (${this.file().name()})"
    return "${this.scope().stringify()} => ${this.ent().stringify()} at $where"
}