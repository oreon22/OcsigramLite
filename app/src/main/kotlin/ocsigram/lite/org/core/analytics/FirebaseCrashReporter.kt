package ocsigram.lite.org.core.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseCrashReporter(private val crashlytics: FirebaseCrashlytics) : CrashReporter {
    override fun recordNonFatal(t: Throwable) {
        crashlytics.recordException(t)
    }
}
