package understand.models

import java.nio.file.Paths

class DirResolver {
    fun resolveRelativePath(relativePath: String): String {
        val projectRoot = System.getProperty("user.dir")

        val p = Paths.get(projectRoot, relativePath)
        return p.toString()
    }
}