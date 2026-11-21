package ocsigram.lite.org.data.auth

import kotlinx.coroutines.flow.StateFlow
import org.drinkless.tdlib.TdApi
import ocsigram.lite.org.core.tdlib.TdGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val gateway: TdGateway) {
    val state: StateFlow<AuthState> = gateway.authState

    suspend fun sendPhone(phone: String) {
        gateway.send(TdApi.SetAuthenticationPhoneNumber(phone, null))
    }

    suspend fun sendCode(code: String) {
        gateway.send(TdApi.CheckAuthenticationCode(code))
    }

    suspend fun sendPassword(password: String) {
        gateway.send(TdApi.CheckAuthenticationPassword(password))
    }

    suspend fun register(firstName: String, lastName: String) {
        gateway.send(TdApi.RegisterUser(firstName, lastName, false))
    }

    suspend fun logOut() {
        gateway.send(TdApi.LogOut())
    }

    fun formatError(error: Throwable): String =
        error.message?.let { message ->
            when {
                message.contains("PHONE_NUMBER_INVALID") ->
                    "Номер телефона указан неверно"
                message.contains("PHONE_CODE_INVALID") ->
                    "Код указан неверно"
                message.contains("PHONE_CODE_EXPIRED") ->
                    "Срок действия кода истёк"
                message.contains("PASSWORD_HASH_INVALID") ->
                    "Пароль указан неверно"
                Regex("FLOOD_WAIT_(\\d+)").containsMatchIn(message) ->
                    "Попробуйте снова через ${
                        Regex("FLOOD_WAIT_(\\d+)").find(message)?.groupValues?.get(1)
                    } сек."
                message.contains("NETWORK") ->
                    "Проверьте подключение к сети"
                else -> "Ошибка: ${error.message}"
            }
        } ?: "Неизвестная ошибка"
}
