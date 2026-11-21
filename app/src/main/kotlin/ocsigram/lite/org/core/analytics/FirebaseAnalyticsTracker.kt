package ocsigram.lite.org.core.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle

class FirebaseAnalyticsTracker(private val analytics: FirebaseAnalytics) : AnalyticsTracker {
    override fun screen(name: String) {
        analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Bundle().apply { putString(FirebaseAnalytics.Param.SCREEN_NAME, name) },
        )
    }

    override fun authError(code: Int) {
        analytics.logEvent(
            "auth_error",
            Bundle().apply { putLong("error_code", code.toLong()) },
        )
    }
}
