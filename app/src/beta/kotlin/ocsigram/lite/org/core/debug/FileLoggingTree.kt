package ocsigram.lite.org.core.debug

import android.content.Context
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicReference

object FileLoggingTree {
    private val writer = AtomicReference<AppLogWriter?>()

    fun install(context: Context) {
        val logDirectory = context.filesDir.resolve("logs").apply { mkdirs() }
        if (writer.get() == null) {
            writer.set(AppLogWriter(logDirectory))
            Timber.plant(writer.get()!!)
        }
    }

    fun logsDirectory(context: Context): File =
        context.filesDir.resolve("logs").apply { mkdirs() }

    fun clear(context: Context) {
        logsDirectory(context).listFiles()?.forEach { it.delete() }
    }
}
