package ocsigram.lite.org.core.analytics

class NoOpCrashReporter : CrashReporter {
    override fun recordNonFatal(t: Throwable) = Unit
}
