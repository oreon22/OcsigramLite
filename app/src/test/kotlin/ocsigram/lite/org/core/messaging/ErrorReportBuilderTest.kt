package ocsigram.lite.org.core.messaging

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorReportBuilderTest {
    @Test
    fun masksPhoneAndLimitsStackAndOmitsPhone() {
        val throwable = Exception("Ошибка")
        val report = ErrorReportBuilder().build(
            context = "auth.submitCode",
            throwable = throwable,
            appVersion = "1",
            flavor = "beta",
            buildType = "debug",
            androidVersion = "15",
            model = "Test",
            abi = "arm64-v8a",
            tdlibVersion = "1.8",
            authState = "WaitCode",
            betaLogText = "номер +79991234567",
        )
        assertTrue(report.contains("***"))
        assertFalse(report.contains("79991234567"))
        val stackSection = report.substringAfter("Stack:\n", "")
        assertTrue(stackSection.lines().size <= ErrorReportBuilder.STACK_LIMIT)
    }
}
