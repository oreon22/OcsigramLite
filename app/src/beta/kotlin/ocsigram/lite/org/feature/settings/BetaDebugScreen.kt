package ocsigram.lite.org.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ocsigram.lite.org.BuildConfig

@Composable
fun BetaDebugScreen() {
    Column(Modifier.padding(16.dp)) {
        Text("Версия сборки: ${BuildConfig.VERSION_NAME}")
        Text("Flavor: beta")
        Text("Версия TDLib: из GetOption(\"version\")")
    }
}
