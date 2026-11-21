package ocsigram.lite.org.core.analytics

class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun screen(name: String) = Unit
    override fun authError(code: Int) = Unit
}
