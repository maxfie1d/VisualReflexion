package understand.models.entities

import com.scitools.understand.Entity

class FileEntity(
        _entity: Entity

) : EntityBase(_entity) {

    init {

    }

    fun getAllFunctions(): List<FunctionEntity> {
        return this.refs("Define", "Function").map { FunctionEntity(it.ent()) }
    }

    fun getMainFunction(entryPointFunctionName: String = "main"): FunctionEntity {
        return this.getAllFunctions().first { it.name() == entryPointFunctionName }
    }

    /**
     * ファイル拡張子を取得
     */
    fun getExtension(): String {
        val name = this._entity.name()
        val index = name.lastIndexOf(".")
        return if (index == -1 || index == name.length - 1) {
            ""
        } else {
            name.substring(index + 1)
        }
    }

}