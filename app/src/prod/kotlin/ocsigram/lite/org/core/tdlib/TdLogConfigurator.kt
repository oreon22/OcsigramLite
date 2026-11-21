package ocsigram.lite.org.core.tdlib

import android.content.Context
import org.drinkless.tdlib.Client
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdTdLogConfigurator @Inject constructor() : TdLogConfigurator {
    override fun configure(context: Context) {
        Client.execute(org.drinkless.tdlib.TdApi.SetLogVerbosityLevel(1))
        Client.execute(org.drinkless.tdlib.TdApi.SetLogStream(org.drinkless.tdlib.TdApi.LogStreamDefault()))
    }
}
