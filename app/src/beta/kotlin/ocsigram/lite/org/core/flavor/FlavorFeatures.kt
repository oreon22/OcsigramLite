package ocsigram.lite.org.core.flavor

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ocsigram.lite.org.feature.settings.BetaLogsScreen
import ocsigram.lite.org.feature.settings.BetaDebugScreen
import ocsigram.lite.org.feature.settings.BetaLogsRoute
import ocsigram.lite.org.feature.settings.BetaDebugRoute
import ocsigram.lite.org.core.debug.FileLoggingTree
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetaFlavorFeatures @Inject constructor(
    @ApplicationContext private val context: Context,
) : ocsigram.lite.org.core.flavor.FlavorFeatures {
    override fun configure(context: Context) {
        ocsigram.lite.org.core.debug.FileLoggingTree.install(context)
    }

    override fun recentLogText(): String {
        val directory = FileLoggingTree.logsDirectory(context)
        val files = directory.listFiles()?.filter { it.name.startsWith("app.log") }?.sortedByDescending { it.name } ?: return ""
        return files.asSequence()
            .flatMap { it.readLines().asSequence() }
            .takeLast(200)
            .joinToString("\n")
            .let { if (it.length > 50 * 1024) it.takeLast(50 * 1024) else it }
    }

    @Composable
    override fun ExtraDestinations() {
        Unit
    }

    @Composable override fun BetaLogsScreen(onBack: () -> Unit) = BetaLogsRoute(onBack)
    @Composable override fun BetaDebugScreen(onBack: () -> Unit) = BetaDebugRoute(onBack)
}
