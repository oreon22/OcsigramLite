package ocsigram.lite.org.core.tdlib

import android.os.Build
import android.util.Log

object TdlibNative {
    private const val TAG = "TdlibNative"

    @Volatile
    private var loaded = false

    fun ensureLoaded() {
        if (loaded) return

        synchronized(this) {
            if (loaded) return

            try {
                System.loadLibrary("tdjni")
                loaded = true
            } catch (error: UnsatisfiedLinkError) {
                val supportedAbis = Build.SUPPORTED_ABIS.joinToString()
                Log.e(TAG, "Unable to load libtdjni.so. Supported device ABIs: $supportedAbis", error)
                throw error
            }
        }
    }
}