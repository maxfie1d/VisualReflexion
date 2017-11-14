package understand.models.entities.entensions

import com.scitools.understand.Entity


fun Entity.stringify(): String {
    return "${this.name()}<${this.kind().name()}>"
}
