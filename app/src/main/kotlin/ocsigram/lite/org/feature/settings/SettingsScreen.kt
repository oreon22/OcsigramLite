package ocsigram.lite.org.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ocsigram.lite.org.BuildConfig
import ocsigram.lite.org.R
import ocsigram.lite.org.data.settings.ThemeMode
import ocsigram.lite.org.core.flavor.FlavorFeatures
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = { IconButton(onClick = onBack) { Text("‹") } },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(stringResource(R.string.theme))
            listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK).forEach { mode ->
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.setTheme(mode) },
                ) {
                    Text(
                        when (mode) {
                            ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                            ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                            ThemeMode.DARK -> stringResource(R.string.theme_dark)
                        },
                    )
                }
            }
            Text(stringResource(R.string.dynamic_colors))
            Switch(
                checked = state.dynamicColors,
                onCheckedChange = viewModel::setDynamicColors,
            )
            Text(stringResource(R.string.about))
            Text("OcsiGram ${BuildConfig.VERSION_NAME}")
            Text("Flavor: ${if (BuildConfig.IS_BETA) "beta" else "prod"}")
            Text("Build type: ${BuildConfig.BUILD_TYPE}")
            Text("TDLib: ${state.tdlibVersion}")
            viewModel.flavorFeatures.ExtraDestinations()
        }
    }
}
