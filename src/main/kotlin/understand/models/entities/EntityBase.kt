package understand.models.entities

import com.scitools.understand.Entity
import com.scitools.understand.Reference
import understand.models.entities.entensions.stringify

open class EntityBase(
        val _entity: Entity
) {
    fun name(): String {
        return this._entity.name()
    }

    /**
     * Entityを一意に表すID
     */
    fun id(): String {
        // Understand APIが算出するIDをそのまま使っている
        // もしIDが衝突することがあれば、srcSlice-forkで行ったID算出方法をとればよい
        // 要するにIDの算出方法は関係なく、IDが一意であればよい
        return this._entity.id().toString()
    }

    fun kind(): String {
        return this._entity.kind().name()
    }

    fun refs(refKindString: String?, entityKindString: String?, unique: Boolean = false): Array<Reference> {
        return this._entity.refs(refKindString, entityKindString, unique)
    }

    fun informationBrowerStrings(): String {
        return this._entity.ib("").joinToString("")
    }

    override fun toString(): String {
        return this._entity.stringify()
    }
}