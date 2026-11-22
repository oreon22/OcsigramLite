package ocsigram.lite.org.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import ocsigram.lite.org.ui.theme.OcsiGramTheme
import ocsigram.lite.org.ui.components.PrimaryButton

@Composable
fun BetaDebugRoute(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val testMessage = stringResource(R.string.debug_test_message)
    BetaDebugScreen(
        state = state,
        onBack = onBack,
        onTestError = { viewModel.messenger.showError("debug.testError", IllegalStateException("Test error")) },
        onTestInfo = { viewModel.messenger.showInfo(testMessage) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetaDebugScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onTestError: () -> Unit = {},
    onTestInfo: () -> Unit = {},
) {
    val rows = listOf(
        stringResource(R.string.debug_version) to BuildConfig.VERSION_NAME,
        stringResource(R.string.debug_flavor) to "beta",
        stringResource(R.string.tdlib_version) to state.tdlibVersion,
        stringResource(R.string.debug_auth_state) to state.authState,
        stringResource(R.string.debug_abi) to state.abi,
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.debug)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(rows, key = { it.first }) { (key, value) ->
                ListItem(headlineContent = { Text(key) }, supportingContent = { Text(value) })
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    PrimaryButton(
                        text = stringResource(R.string.debug_test_error),
                        onClick = onTestError,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    PrimaryButton(
                        text = stringResource(R.string.debug_test_message),
                        onClick = onTestInfo,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BetaDebugPreview() {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(false) },
    ) { BetaDebugScreen(SettingsUiState(tdlibVersion = "1.8", authState = "WaitPhone"), {}) }
}
