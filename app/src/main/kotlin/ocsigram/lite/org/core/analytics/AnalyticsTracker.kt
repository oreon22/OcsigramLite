package ocsigram.lite.org.core.analytics

interface AnalyticsTracker {
    fun screen(name: String)
    fun authError(code: Int)
}
