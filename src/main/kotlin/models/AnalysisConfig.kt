package models

import com.google.gson.annotations.SerializedName

data class ModuleMapping(
        val name: String,
        val map_to: String
)

/**
 * 条件式をTrueもしくはFalseに設定するために情報
 */
class ConditionData(
        @SerializedName("file")
        val filePath: String,

        @SerializedName("line_number")
        val lineNumber: Int,

        @SerializedName("value")
        val value: Boolean
) {

    val isValueFalse: Boolean
        get() = !this.value
}

/**
 * 設定ファイルに書くJSONをこのオブジェクトに落とし込む
 */
data class AnalysisConfig(
        @SerializedName("project_name")
        val projectName: String,

        @SerializedName("entry_point_func_name")
        val entryPointFuncName: String,

        @SerializedName("target_variables")
        val targetVariables: List<String>,

        @SerializedName("module_mappings")
        val moduleMappings: List<ModuleMapping>,

        @SerializedName("controls")
        val controls: List<ConditionData>
)
