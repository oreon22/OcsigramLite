package ocsigram.lite.org.core.messaging

data class AppMessage(
    val text: String,
    val isError: Boolean,
    val report: String?,
)
