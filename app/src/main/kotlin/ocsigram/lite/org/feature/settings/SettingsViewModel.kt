package ocsigram.lite.org.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ocsigram.lite.org.core.tdlib.TdGateway
import ocsigram.lite.org.core.flavor.FlavorFeatures
import ocsigram.lite.org.data.settings.SettingsRepository
import ocsigram.lite.org.data.settings.ThemeMode
import javax.inject.Inject

data class SettingsUiState(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = true,
    val tdlibVersion: String = "Unknown",
    val authState: String = "Unknown",
    val abi: String = android.os.Build.SUPPORTED_ABIS.firstOrNull().orEmpty(),
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val gateway: TdGateway,
    val flavorFeatures: FlavorFeatures,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            gateway.authState.collect { state ->
                _uiState.value = _uiState.value.copy(authState = state::class.simpleName.orEmpty())
            }
        }
        viewModelScope.launch {
            settings.theme.collect { theme ->
                _uiState.value = _uiState.value.copy(theme = theme)
            }
        }
        viewModelScope.launch {
            settings.dynamicColors.collect { enabled ->
                _uiState.value = _uiState.value.copy(dynamicColors = enabled)
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tdlibVersion = gateway.getTdlibVersion())
        }
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { settings.setTheme(mode) }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch { settings.setDynamicColors(enabled) }
    }
}
