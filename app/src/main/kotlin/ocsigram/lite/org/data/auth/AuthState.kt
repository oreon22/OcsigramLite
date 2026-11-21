package ocsigram.lite.org.data.auth

sealed interface AuthState {
    data object Loading : AuthState
    data object WaitPhone : AuthState
    data object WaitCode : AuthState
    data class WaitPassword(val hint: String) : AuthState
    data object WaitRegistration : AuthState
    data object Ready : AuthState
    data object LoggingOut : AuthState
    data object Closed : AuthState
    data class Unsupported(val stateName: String) : AuthState
}
