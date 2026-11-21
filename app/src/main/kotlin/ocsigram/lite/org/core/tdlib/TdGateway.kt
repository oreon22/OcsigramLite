package ocsigram.lite.org.core.tdlib

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.drinkless.tdlib.TdApi
import ocsigram.lite.org.data.auth.AuthState

interface TdGateway {
    val updates: SharedFlow<TdApi.Update>
    val authState: StateFlow<AuthState>
    suspend fun <T : TdApi.Object> send(function: TdApi.Function<T>): T
    suspend fun getTdlibVersion(): String
}
