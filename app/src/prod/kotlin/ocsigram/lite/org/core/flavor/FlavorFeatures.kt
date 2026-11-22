package ocsigram.lite.org.core.flavor

import android.content.Context
import androidx.compose.runtime.Composable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdFlavorFeatures @Inject constructor() : ocsigram.lite.org.core.flavor.FlavorFeatures {
    override fun configure(context: Context) = Unit

    @Composable
    override fun ExtraDestinations() = Unit

    @Composable override fun BetaLogsScreen(onBack: () -> Unit) = Unit
    @Composable override fun BetaDebugScreen(onBack: () -> Unit) = Unit
}
