package ocsigram.lite.org.core.analytics

interface CrashReporter {
    fun recordNonFatal(t: Throwable)
}
