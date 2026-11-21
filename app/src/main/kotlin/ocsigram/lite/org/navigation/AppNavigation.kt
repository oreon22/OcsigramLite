package ocsigram.lite.org.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ocsigram.lite.org.feature.auth.AuthScreen
import ocsigram.lite.org.feature.home.HomeScreen
import ocsigram.lite.org.feature.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: ocsigram.lite.org.feature.auth.AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val start = if (authState.isReady) "home" else "auth"

    LaunchedEffect(authState.isReady) {
        navController.navigate(if (authState.isReady) "home" else "auth") {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = start) {
        composable("auth") { AuthScreen(authViewModel) }
        composable("home") { HomeScreen(onSettings = { navController.navigate("settings") }) }
        composable("settings") { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}
