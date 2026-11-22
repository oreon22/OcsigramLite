package ocsigram.lite.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ocsigram.lite.org.data.settings.SettingsRepository
import ocsigram.lite.org.navigation.AppNavigation
import ocsigram.lite.org.ui.theme.OcsiGramTheme
import ocsigram.lite.org.core.messaging.AppMessenger
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var appMessenger: AppMessenger

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            OcsiGramTheme(
                darkTheme = settingsRepository.darkTheme.collectAsStateWithLifecycle(
                    initialValue = null,
                    lifecycle = lifecycle,
                ),
                dynamicColor = settingsRepository.dynamicColors.collectAsStateWithLifecycle(
                    initialValue = true,
                    lifecycle = lifecycle,
                ),
            ) {
                AppNavigation(appMessenger)
            }
        }
    }
}
