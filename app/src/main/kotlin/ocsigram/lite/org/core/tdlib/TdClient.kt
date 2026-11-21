package ocsigram.lite.org.core.tdlib

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import ocsigram.lite.org.BuildConfig
import ocsigram.lite.org.data.auth.AuthState
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class TdClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logConfigurator: TdLogConfigurator,
) : TdGateway, Client.ResultHandler {
    private val updateFlow = MutableSharedFlow<TdApi.Update>(extraBufferCapacity = 4096)
    private val authFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val pending = ConcurrentHashMap<Long, CancellableContinuation<TdApi.Object>>()
    private var client: Client? = null
    private var nextRequestId = 1L

    override val updates: SharedFlow<TdApi.Update> = updateFlow
    override val authState: StateFlow<AuthState> = authFlow

    init {
        TdlibNative.ensureLoaded()
        createClient()
    }

    private fun createClient() {
        logConfigurator.configure(context)
        client = Client.create(this, null, null)
    }

    override fun onResult(result: TdApi.Object) {
        when (result) {
            is TdApi.UpdateAuthorizationState -> handleAuthorizationState(result.authorizationState)
            is TdApi.Update -> {
                updateFlow.tryEmit(result)
            }
            else -> Unit
        }
    }

    override suspend fun <T : TdApi.Object> send(function: TdApi.Function<T>): T =
        suspendCancellableCoroutine<T> { continuation ->
            val id = nextRequestId++
            pending[id] = continuation as CancellableContinuation<TdApi.Object>
            val resultHandler = Client.ResultHandler { result ->
                val waiter = pending.remove(id)
                when (result) {
                    is TdApi.Error ->
                        waiter?.resumeWith(Result.failure(TdException(result.code, result.message)))
                    else -> waiter?.resume(result)
                }
            }
            client?.send(function, resultHandler, null)
            continuation.invokeOnCancellation { pending.remove(id) }
        } as T

    override suspend fun getTdlibVersion(): String = withContext(Dispatchers.IO) {
        val result = Client.execute(TdApi.GetOption("version"))
        if (result is TdApi.OptionValueString) result.value else "неизвестно"
    }

    private fun handleAuthorizationState(state: TdApi.AuthorizationState) {
        authFlow.value = when (state) {
            is TdApi.AuthorizationStateWaitPhoneNumber -> AuthState.WaitPhone
            is TdApi.AuthorizationStateWaitCode -> AuthState.WaitCode
            is TdApi.AuthorizationStateWaitPassword -> AuthState.WaitPassword(state.passwordHint)
            is TdApi.AuthorizationStateWaitRegistration -> AuthState.WaitRegistration
            is TdApi.AuthorizationStateReady -> AuthState.Ready
            is TdApi.AuthorizationStateLoggingOut -> AuthState.LoggingOut
            is TdApi.AuthorizationStateClosed -> {
                createClient()
                AuthState.Closed
            }
            is TdApi.AuthorizationStateWaitTdlibParameters -> {
                sendParameters()
                AuthState.Loading
            }
            is TdApi.AuthorizationStateWaitEmailAddress,
            is TdApi.AuthorizationStateWaitEmailCode,
            is TdApi.AuthorizationStateWaitOtherDeviceConfirmation ->
                AuthState.Unsupported(state::class.simpleName ?: "Unsupported")
            else -> AuthState.Unsupported(state::class.simpleName ?: "Unsupported")
        }
    }

    private fun sendParameters() {
        val parameters = TdApi.SetTdlibParameters().apply {
            useTestDc = false
            databaseDirectory = context.filesDir.resolve("tdlib").absolutePath
            filesDirectory = context.filesDir.resolve("tdlib/files").absolutePath
            databaseEncryptionKey = byteArrayOf()
            useFileDatabase = true
            useChatInfoDatabase = true
            useMessageDatabase = true
            useSecretChats = false
            apiId = BuildConfig.TDLIB_API_ID
            apiHash = BuildConfig.TDLIB_API_HASH
            systemLanguageCode = java.util.Locale.getDefault().language.ifEmpty { "ru" }
            deviceModel = android.os.Build.MODEL.ifEmpty { "Android" }
            systemVersion = android.os.Build.VERSION.RELEASE
            applicationVersion = BuildConfig.VERSION_NAME
        }
        client?.send(parameters, null, null)
    }
}
