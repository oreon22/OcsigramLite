package ocsigram.lite.org.core.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import org.drinkless.tdlib.TdApi
import ocsigram.lite.org.core.tdlib.TdGateway
import javax.inject.Inject

@AndroidEntryPoint
class OcsiFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var gateway: TdGateway

    override fun onNewToken(token: String) {
        gateway.runCatching {
            TdApi.DeviceTokenFirebaseCloudMessaging(token, false)
        }
        // TODO: register after TDLib API signatures are checked against copied TdApi.java.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val payload = message.data.toMutableMap().apply {
            put("google.sent_time", message.sentTime.toString())
            put("google.notification.sound", "default")
        }
        // TODO: gateway.send(TdApi.ProcessPushNotification(org.json.JSONObject(payload).toString()))
    }
}
