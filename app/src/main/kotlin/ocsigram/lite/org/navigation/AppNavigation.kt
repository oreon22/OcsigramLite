package ocsigram.lite.org.navigation

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.PersistableBundle
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ocsigram.lite.org.feature.auth.AuthRoute
import ocsigram.lite.org.feature.home.HomeScreen
import ocsigram.lite.org.feature.settings.SettingsRoute
import ocsigram.lite.org.feature.settings.SettingsViewModel
import ocsigram.lite.org.core.flavor.FlavorFeatures
import ocsigram.lite.org.core.messaging.AppMessenger
import ocsigram.lite.org.data.auth.AuthState
import ocsigram.lite.org.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun AppNavigation(messenger: AppMessenger) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val flavorFeatures: FlavorFeatures = settingsViewModel.flavorFeatures
    val authViewModel = hiltViewModel<ocsigram.lite.org.feature.auth.AuthViewModel>()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val start = if (authState.isReady) "home" else "auth"
    var previousAuthState by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(authState.isReady) {
        navController.navigate(if (authState.isReady) "home" else "auth") {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(messenger) {
        messenger.messages.collect { message ->
            if (message.text.isBlank()) {
                snackbarHostState.currentSnackbarData?.dismiss()
            } else {
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message.text,
                        actionLabel = if (message.isError && message.report != null)
                            context.getString(R.string.copy_log) else null,
                        withDismissAction = message.isError,
                        duration = if (message.isError) SnackbarDuration.Long else SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed && message.report != null) {
                        val clipboard = context.getSystemService(ClipboardManager::class.java)
                        val description = ClipDescription(
                            context.getString(R.string.error_report),
                            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        )
                        description.extras = PersistableBundle().apply {
                            putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                        }
                        clipboard.setPrimaryClip(ClipData(description, ClipData.Item(message.report)))
                    }
                }
            }
        }
    }

    LaunchedEffect(authState.authState) {
        val current = authState.authState
        if (current is AuthState.Ready) {
            messenger.dismissCurrent()
            val shouldShow = previousAuthState == AuthState.WaitPhone::class.simpleName ||
                previousAuthState == AuthState.WaitCode::class.simpleName ||
                previousAuthState == AuthState.WaitPassword::class.simpleName ||
                previousAuthState == AuthState.WaitRegistration::class.simpleName
            if (shouldShow) messenger.showInfo(context.getString(R.string.signed_in))
        }
        previousAuthState = current::class.simpleName
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = data.visuals.actionLabel == context.getString(R.string.copy_log)
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                    actionColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = start,
        ) {
            composable("auth") { AuthRoute(authViewModel) }
            composable("home") { HomeScreen(onSettings = { navController.navigate("settings") }) }
            composable("settings") {
                SettingsRoute(
                    onBack = { navController.popBackStack() },
                    onLogs = { navController.navigate("beta_logs") },
                    onDebug = { navController.navigate("beta_debug") },
                )
            }
            composable("beta_logs") { flavorFeatures.BetaLogsScreen { navController.popBackStack() } }
            composable("beta_debug") { flavorFeatures.BetaDebugScreen { navController.popBackStack() } }
        }
    }
}
