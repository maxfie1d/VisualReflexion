package understand.models.entities

import com.scitools.understand.Entity
import com.scitools.understand.Reference
import data.ProgramPoint
import data.ProgramRange

class FunctionEntity(
        _entity: Entity
) : EntityBase(_entity) {
    fun getAllVariables(): List<Entity> {
        return this._entity.refs("", "Local Object", true).map { it.ent() }
    }

    fun getAllParameters(): List<Entity> {
        return this._entity.refs("", "Parameter", true).map { it.ent() }
    }

    fun getStructVariables(): List<Entity> {
        return this.refs("", "Public Object", true).map { it.ent() }
    }

    /**
     * 関数が定義されているファイルを返す
     */
    fun getDeclaredFile(): FileEntity {
        val ref = this.refs("Definein", "File").first()
        return FileEntity(ref.ent())
    }

    /**
     * 関数が宣言されている範囲を取得
     */
    fun getDeclaredRange(): ProgramRange {
        val first = this.refs("Definein", "File").first()

        // 参照関係Endは自身の関数への参照を表し、何行目で関数宣言が終わっているかがわかる
        val end = this.refs("End", "function").first()
        return ProgramRange(first.line(), end.line())
    }

    fun calledFunctionRefs(): List<Reference> {
        return this.refs("Callby", "Function").toList()
    }

    /**
     * 関数が呼ばれた位置のリストを返す
     */
    fun calledProgramPoints(): List<ProgramPoint> {
        return this.refs("Callby", "Function").map {
            ProgramPoint(
                    it.line(),
                    it.ent().id().toString()
            )
        }
    }
}
