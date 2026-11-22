package ocsigram.lite.org.core.flavor

import android.content.Context
import androidx.compose.runtime.Composable

interface FlavorFeatures {
    fun configure(context: Context)
    fun recentLogText(): String = ""
    @Composable fun ExtraDestinations()
    @Composable fun BetaLogsScreen(onBack: () -> Unit)
    @Composable fun BetaDebugScreen(onBack: () -> Unit)
}
