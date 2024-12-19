package top.fifthlight.touchcontroller.proxy.client.android

import android.os.Vibrator
import android.util.Log
import top.fifthlight.touchcontroller.proxy.client.LauncherProxyClient
import top.fifthlight.touchcontroller.proxy.message.VibrateMessage

private val TAG = "SimpleVibrationHandler"

class SimpleVibrationHandler(private val service: Vibrator) : LauncherProxyClient.VibrationHandler {
    override fun viberate(kind: VibrateMessage.Kind) {
        try {
            @Suppress("DEPRECATION")
            service.vibrate(100)
        } catch (ex: Exception) {
            Log.w(TAG, "Failed to trigger vibration", ex)
        }
    }
}