package ocsigram.lite.org.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ocsigram.lite.org.data.auth.AuthRepository
import ocsigram.lite.org.data.auth.AuthState
import javax.inject.Inject

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val phone: String = "",
    val code: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val error: String? = null,
    val busy: Boolean = false,
) {
    val isReady: Boolean = authState is AuthState.Ready
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.state.collect { state ->
                _uiState.update { it.copy(authState = state, busy = false) }
            }
        }
    }

    fun phoneChanged(value: String) = _uiState.update { it.copy(phone = value, error = null) }
    fun codeChanged(value: String) = _uiState.update { it.copy(code = value, error = null) }
    fun passwordChanged(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun firstNameChanged(value: String) = _uiState.update { it.copy(firstName = value, error = null) }
    fun lastNameChanged(value: String) = _uiState.update { it.copy(lastName = value, error = null) }

    fun submit() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(busy = true, error = null) }
            runCatching {
                when (state.authState) {
                    is AuthState.WaitPhone -> repository.sendPhone(state.phone)
                    is AuthState.WaitCode -> repository.sendCode(state.code)
                    is AuthState.WaitPassword -> repository.sendPassword(state.password)
                    is AuthState.WaitRegistration ->
                        repository.register(state.firstName, state.lastName)
                    else -> Unit
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        busy = false,
                        error = repository.formatError(error),
                    )
                }
            }
        }
    }
}
