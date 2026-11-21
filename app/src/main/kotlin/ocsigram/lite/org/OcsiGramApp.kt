package ocsigram.lite.org

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.StrictMode
import androidx.core.content.getSystemService
import dagger.hilt.android.HiltAndroidApp
import ocsigram.lite.org.core.flavor.FlavorFeatures
import javax.inject.Inject

@HiltAndroidApp
class OcsiGramApp : Application() {
    @Inject
    lateinit var flavorFeatures: FlavorFeatures

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.IS_BETA) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
        }
        flavorFeatures.configure(this)
        val channel = NotificationChannel(
            "ocsigram_notifications",
            getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        getSystemService<NotificationManager>()?.createNotificationChannel(channel)
    }
}
