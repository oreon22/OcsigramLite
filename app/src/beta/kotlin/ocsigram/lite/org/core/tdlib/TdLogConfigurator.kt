package ocsigram.lite.org.core.tdlib

import android.content.Context
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetaTdLogConfigurator @Inject constructor() : TdLogConfigurator {
    override fun configure(context: Context) {
        Client.execute(TdApi.SetLogVerbosityLevel(4))
        val logDirectory = context.filesDir.resolve("logs").apply { mkdirs() }
        Client.execute(
            TdApi.SetLogStream(
                TdApi.LogStreamFile(
                    logDirectory.resolve("tdlib.log").absolutePath,
                    2L * 1024 * 1024,
                    true,
                ),
            ),
        )
    }
}
