package file

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import data.FunctionData
import data.VariableData
import models.ControlData
import models.ControlTable
import models.FunctionTable
import models.VariableTable
import java.io.File

data class SliceOutputJsonObject(
        @SerializedName("vars")
        val vars: String,
        @SerializedName("funcs")
        val funcs: String,
        @SerializedName("controls")
        val controls: String
)

data class SliceOutput(
        val variableTable: VariableTable,
        val functionTable: FunctionTable,
        val controlTable: ControlTable
)

class SliceOutputLoader(private val file: File)
    : LoaderBase {
    override fun load(): SliceOutput? {
        return try {
            val a = Gson().fromJson(file.readText(), SliceOutputJsonObject::class.java)
            SliceOutput(
                    loadVars(a.vars),
                    loadFuncs(a.funcs),
                    loadControls(a.controls)
            )
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * CSV形式の変数表を読み込んで内部表現に変換します
     */
    private fun loadVars(varTableAsStr: String): VariableTable {
        val vars = varTableAsStr.split("\n")
                .filter { !it.isEmpty() }
                .drop(1) // ヘッダは飛ばす
                .mapNotNull { VariableData.createFromCsvRow(it) }
        return VariableTable(vars)
    }

    /**
     * CSV形式の関数表を読み込んで内部表現に変換します
     */
    private fun loadFuncs(funcTableAsStr: String): FunctionTable {
        val funcs = funcTableAsStr
                .split("\n")
                .filter { !it.isEmpty() }
                .drop(1)
                .map { FunctionData.createFromCsvRow(it) }
        return FunctionTable(funcs)
    }

    /**
     * CSV形式の制御表を読み込んで内部表現に変換します
     */
    private fun loadControls(controlTableAsStr: String): ControlTable {
        val controls = controlTableAsStr
                .split("\n")
                .filterNot { it.isEmpty() }
                .drop(1)
                .map { ControlData.createFromCsvRow(it) }
        return ControlTable(controls)
    }
}
