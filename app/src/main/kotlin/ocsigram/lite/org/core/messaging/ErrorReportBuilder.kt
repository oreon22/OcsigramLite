package ocsigram.lite.org.core.messaging

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ErrorReportBuilder {
    fun build(
        context: String,
        throwable: Throwable?,
        appVersion: String,
        flavor: String,
        buildType: String,
        androidVersion: String,
        model: String,
        abi: String,
        tdlibVersion: String,
        authState: String,
        betaLogText: String = "",
    ): String {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())
        val exception = throwable?.let {
            "\nException: ${it.javaClass.name}\nException text: ${it.message.orEmpty()}"
        }.orEmpty()
        val stack = throwable?.stackTrace?.take(STACK_LIMIT)?.joinToString("\n") {
            "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
        }.orEmpty()
        val log = betaLogText.lines().takeLast(LOG_LIMIT).joinToString("\n").let {
            if (it.length > MAX_LOG_BYTES) it.takeLast(MAX_LOG_BYTES) else it
        }
        val report = buildString {
            append("Time: ").append(time)
            append("\nApp version: ").append(appVersion)
            append("\nFlavor: ").append(flavor)
            append("\nBuild type: ").append(buildType)
            append("\nAndroid: ").append(androidVersion)
            append("\nModel: ").append(model)
            append("\nABI: ").append(abi)
            append("\nTDLib: ").append(tdlibVersion)
            append("\nAuth state: ").append(authState)
            append("\nContext: ").append(context)
            append(exception)
            if (stack.isNotBlank()) append("\nStack:\n").append(stack)
            if (log.isNotBlank()) append("\nRecent logs:\n").append(log)
        }
        return PHONE_MASK.replace(report, "***")
    }

    companion object {
        const val STACK_LIMIT = 30
        const val LOG_LIMIT = 200
        const val MAX_LOG_BYTES = 50 * 1024
        val PHONE_MASK = Regex("\\d{7,}")
    }
}
