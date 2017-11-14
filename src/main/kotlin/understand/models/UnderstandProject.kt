package understand.models

import com.scitools.understand.Database
import com.scitools.understand.Understand
import models.FunctionTable
import models.VariableTable
import understand.models.entities.FileEntity
import understand.models.entities.VariableEntity
import understand.models.entities.entensions.toFunctionData
import understand.models.entities.entensions.toVariableData
import java.io.File

// Understand Databseのラッパー

class UnderstandProject(
        val _projectFile: File
) {
    private val _db: Database = Understand.open(this._projectFile.absolutePath)

    fun getFileEntity(fileName: String): FileEntity {
        val entity = this._db.ents("file").first { it.name() == fileName }
        return FileEntity(entity)
    }

    fun getAllFiles(): List<FileEntity> {
        return this._db.ents("File").filter {
            // stdio.hやmath.hはkindがUnknown Fileになっている
            it.kind().name() != "Unknown File"
        }.map(::FileEntity)
    }

    fun getMainFile(mainFileName: String = "main.cpp"): FileEntity {
        return this.getFileEntity(mainFileName)
    }

    fun dispose() {
        this._db.close()
    }

    fun getFunctionTable(): FunctionTable {
        val allFiles = this.getAllFiles()

        val allFunctions = allFiles.filter { it.getExtension() == "c" }.map {
            it.getAllFunctions()
        }.flatten().map { it.toFunctionData() }

        return FunctionTable(allFunctions)
    }

    /**
     * すべてのグローバル変数を取得する
     */
    private fun getAllGlobalVariables(): List<VariableEntity> {
        return this.getAllFiles().flatMap {
            it.refs("", "Global Object")
                    .map { VariableEntity(it.ent()) }
        }.distinctBy { it.id() }
    }

    /**
     * すべてのローカル変数を取得する
     */
    private fun getAllLocalVariables(): List<VariableEntity> {
        return this.getAllFiles().flatMap {
            it.getAllFunctions()
        }.flatMap {
            it.getAllVariables()
        }.distinctBy { it.id() }
                .map(::VariableEntity)
    }

    /**
     * すべての関数の引数を取得する
     */
    private fun getAllFunctionParameters(): List<VariableEntity> {
        return this.getAllFiles().flatMap {
            it.getAllFunctions()
        }.flatMap {
            it.getAllParameters()
        }.distinctBy { it.id() }
                .map(::VariableEntity)
    }

    /**
     * すべての構造体変数のメンバを取得する
     */
    private fun getAllStructMemberVariables(): List<VariableEntity> {
        return this.getAllFiles().flatMap {
            it.getAllFunctions()
        }.flatMap {
            it.getStructVariables()
        }.distinctBy { it.id() }
                .map(::VariableEntity)
    }

    fun getVariableTable(functionTable: FunctionTable): VariableTable {
        // すべての変数を取得する
        val allLocalVariables = this.getAllLocalVariables()
        val allGlobalVariables = this.getAllGlobalVariables()
        val allStructMemberVariables = this.getAllStructMemberVariables()
        val allFunctionParameters = this.getAllFunctionParameters()

        val structMemberVars = allStructMemberVariables.map { it.toVariableData(emptyList()) }

        val allVars = listOf(
                allLocalVariables,
                allGlobalVariables,
                allFunctionParameters
        ).flatten()

        val list = allVars
                .map { it.toVariableData(structMemberVars) }

        return VariableTable(list)
    }
}
