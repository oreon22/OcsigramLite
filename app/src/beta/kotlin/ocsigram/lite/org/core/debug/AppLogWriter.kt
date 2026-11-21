package ocsigram.lite.org.core.debug

import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class AppLogWriter(private val directory: File) : Timber.Tree() {
    private val maxSize = 2 * 1024 * 1024L
    private val maxFiles = 5

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val file = rotateIfNeeded()
        PrintWriter(FileWriter(file, true)).use { writer ->
            writer.println("${System.currentTimeMillis()} $tag $message")
            t?.printStackTrace(writer)
        }
    }

    private fun rotateIfNeeded(): File {
        val current = directory.resolve("app.log")
        if (current.exists() && current.length() >= maxSize) {
            directory.resolve("app.log.4").takeIf { it.exists() }?.delete()
            for (index in 3 downTo 0) {
                val source = if (index == 0) current else directory.resolve("app.log.$index")
                val target = directory.resolve("app.log.${index + 1}")
                if (source.exists()) source.renameTo(target)
            }
        }
        return current
    }
}
