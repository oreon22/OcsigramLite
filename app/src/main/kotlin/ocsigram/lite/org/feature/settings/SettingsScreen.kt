package ocsigram.lite.org.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ocsigram.lite.org.BuildConfig
import ocsigram.lite.org.R
import ocsigram.lite.org.data.settings.ThemeMode
import ocsigram.lite.org.ui.components.SectionHeader
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onLogs: () -> Unit,
    onDebug: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onBack = onBack,
        onTheme = viewModel::setTheme,
        onDynamicColors = viewModel::setDynamicColors,
        onLogs = onLogs,
        onDebug = onDebug,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onTheme: (ThemeMode) -> Unit,
    onDynamicColors: (Boolean) -> Unit,
    onLogs: () -> Unit,
    onDebug: () -> Unit,
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionHeader(stringResource(R.string.theme))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.material3.MaterialTheme.shapes.large,
                color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val modes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
                    val labels = listOf(
                        stringResource(R.string.theme_system),
                        stringResource(R.string.theme_light),
                        stringResource(R.string.theme_dark),
                    )
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        modes.forEachIndexed { index, mode ->
                            SegmentedButton(
                                selected = state.theme == mode,
                                onClick = { onTheme(mode) },
                                shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                            ) { Text(labels[index]) }
                        }
                    }
                    Surface(
                        onClick = { onDynamicColors(!state.dynamicColors) },
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.dynamic_colors)) },
                            supportingContent = { Text(stringResource(R.string.dynamic_colors_support)) },
                            trailingContent = {
                                Switch(
                                    checked = state.dynamicColors,
                                    onCheckedChange = onDynamicColors,
                                )
                            },
                        )
                    }
                }
            }

            SectionHeader(stringResource(R.string.about))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.material3.MaterialTheme.shapes.large,
                color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column {
                    SettingsValue(stringResource(R.string.app_version), "OcsiGram ${BuildConfig.VERSION_NAME}")
                    SettingsValue(stringResource(R.string.flavor), if (BuildConfig.IS_BETA) "beta" else "prod")
                    SettingsValue(stringResource(R.string.build_type), BuildConfig.BUILD_TYPE)
                    SettingsValue(stringResource(R.string.tdlib_version), state.tdlibVersion)
                }
            }

            if (BuildConfig.IS_BETA) {
                SectionHeader(stringResource(R.string.testing))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.material3.MaterialTheme.shapes.large,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Column {
                        SettingsDestination(
                            icon = R.drawable.ic_share,
                            title = stringResource(R.string.logs),
                            onClick = onLogs,
                        )
                        SettingsDestination(
                            icon = R.drawable.ic_settings,
                            title = stringResource(R.string.debug),
                            onClick = onDebug,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsValue(title: String, value: String) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(value) },
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

@Composable
private fun SettingsDestination(icon: Int, title: String, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            leadingContent = {
                Icon(painterResource(icon), contentDescription = title)
            },
            headlineContent = { Text(title) },
            trailingContent = {
                Icon(painterResource(R.drawable.ic_back), contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(false) },
    ) {
        SettingsScreen(
            state = SettingsUiState(theme = ThemeMode.SYSTEM, tdlibVersion = "1.8"),
            onBack = {},
            onTheme = {},
            onDynamicColors = {},
            onLogs = {},
            onDebug = {},
        )
    }
}
