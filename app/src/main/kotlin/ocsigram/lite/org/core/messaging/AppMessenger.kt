package ocsigram.lite.org.core.messaging

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import ocsigram.lite.org.BuildConfig
import ocsigram.lite.org.R
import ocsigram.lite.org.core.flavor.FlavorFeatures
import ocsigram.lite.org.core.tdlib.TdGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMessenger @Inject constructor(
    @ApplicationContext private val context: Context,
    private val flavorFeatures: FlavorFeatures,
    private val gateway: TdGateway,
) {
    private val _messages = MutableSharedFlow<AppMessage>(extraBufferCapacity = 8)
    val messages: SharedFlow<AppMessage> = _messages.asSharedFlow()
    private var lastErrorText: String? = null
    private var lastErrorAt = 0L

    fun showError(contextText: String, throwable: Throwable? = null) {
        val now = System.currentTimeMillis()
        if (lastErrorText == contextText && now - lastErrorAt < 2_000) return
        val report = ErrorReportBuilder().build(
            context = contextText,
            throwable = throwable,
            appVersion = BuildConfig.VERSION_NAME,
            flavor = if (BuildConfig.IS_BETA) "beta" else "prod",
            buildType = BuildConfig.BUILD_TYPE,
            androidVersion = Build.VERSION.RELEASE,
            model = Build.MODEL,
            abi = Build.SUPPORTED_ABIS.firstOrNull().orEmpty(),
            tdlibVersion = runBlocking { gateway.getTdlibVersion() },
            authState = gateway.authState.value::class.simpleName.orEmpty(),
            betaLogText = flavorFeatures.recentLogText(),
        )
        lastErrorText = contextText
        lastErrorAt = now
        _messages.tryEmit(AppMessage(context.getString(R.string.error_default), isError = true, report = report))
    }

    fun showInfo(text: String) {
        _messages.tryEmit(AppMessage(text, isError = false, report = null))
    }

    fun dismissCurrent() {
        _messages.tryEmit(AppMessage("", isError = false, report = null))
    }
}
