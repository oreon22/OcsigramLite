package ocsigram.lite.org.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
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
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var savedPhone
        get() = savedStateHandle.get<String>("phone").orEmpty()
        set(value) = savedStateHandle.set("phone", value)
    private var savedCode
        get() = savedStateHandle.get<String>("code").orEmpty()
        set(value) = savedStateHandle.set("code", value)
    private var savedFirstName
        get() = savedStateHandle.get<String>("firstName").orEmpty()
        set(value) = savedStateHandle.set("firstName", value)
    private var savedLastName
        get() = savedStateHandle.get<String>("lastName").orEmpty()
        set(value) = savedStateHandle.set("lastName", value)

    private val _uiState = MutableStateFlow(
        AuthUiState(
            phone = savedPhone,
            code = savedCode,
            firstName = savedFirstName,
            lastName = savedLastName,
        ),
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.state.collect { state ->
                _uiState.update { it.copy(authState = state, busy = false) }
            }
        }
    }

    fun phoneChanged(value: String) {
        val normalized = if (value.startsWith("+")) {
            "+" + value.drop(1).filter { it.isDigit() || it == ' ' }
        } else {
            value.filter { it.isDigit() || it == ' ' }
        }
        savedPhone = normalized
        _uiState.update { it.copy(phone = normalized, error = null) }
    }
    fun codeChanged(value: String) {
        val filtered = value.filter(Char::isDigit)
        savedCode = filtered
        _uiState.update { it.copy(code = filtered, error = null) }
    }
    fun passwordChanged(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun firstNameChanged(value: String) {
        savedFirstName = value
        _uiState.update { it.copy(firstName = value, error = null) }
    }
    fun lastNameChanged(value: String) {
        savedLastName = value
        _uiState.update { it.copy(lastName = value, error = null) }
    }

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
