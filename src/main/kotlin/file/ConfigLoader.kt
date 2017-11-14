package file

import com.google.gson.Gson
import models.AnalysisConfig
import java.io.File

class ConfigLoader(private val file: File) {
    fun load(): AnalysisConfig? {
        val s = file.readText()
        return try {
            Gson().fromJson(s, AnalysisConfig::class.java)
        } catch (ex: Exception) {
            null
        }
    }
}
