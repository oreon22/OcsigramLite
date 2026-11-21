package ocsigram.lite.org.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.drinkless.tdlib.TdApi
import org.junit.Assert.assertEquals
import org.junit.Test
import ocsigram.lite.org.core.tdlib.TdGateway

class AuthRepositoryTest {
    private class FakeGateway : TdGateway {
        override val updates = MutableSharedFlow<TdApi.Update>(extraBufferCapacity = 1)
        override val authState: StateFlow<AuthState> =
            MutableStateFlow(AuthState.WaitPhone)

        override suspend fun <T : TdApi.Object> send(function: TdApi.Function<T>): T {
            throw UnsupportedOperationException("fake")
        }

        override suspend fun getTdlibVersion(): String = "test"
    }

    @Test
    fun phoneWaitStateIsExposed() = runTest {
        val repository = AuthRepository(FakeGateway())
        assertEquals(AuthState.WaitPhone, repository.state.value)
    }

    @Test
    fun errorsBecomeRussianMessages() {
        val repository = AuthRepository(FakeGateway())
        assertEquals(
            "Код указан неверно",
            repository.formatError(IllegalStateException("PHONE_CODE_INVALID")),
        )
        assertEquals(
            "Попробуйте снова через 30 сек.",
            repository.formatError(IllegalStateException("FLOOD_WAIT_30")),
        )
    }
}
