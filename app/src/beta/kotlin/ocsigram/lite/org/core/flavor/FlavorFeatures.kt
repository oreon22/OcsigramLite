package ocsigram.lite.org.core.flavor

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ocsigram.lite.org.feature.settings.BetaLogsScreen
import ocsigram.lite.org.feature.settings.BetaDebugScreen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetaFlavorFeatures @Inject constructor() : ocsigram.lite.org.core.flavor.FlavorFeatures {
    override fun configure(context: Context) {
        ocsigram.lite.org.core.debug.FileLoggingTree.install(context)
    }

    @Composable
    override fun ExtraDestinations() {
        Column(Modifier.padding(16.dp)) {
            Text("Логи и отладка доступны в настройках.")
            BetaLogsScreen()
            BetaDebugScreen()
        }
    }
}
