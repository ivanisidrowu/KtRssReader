package tw.ktrssreader.utils

import android.os.Looper
import kotlin.concurrent.thread

object ThreadUtils {

    fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    fun runOnNewThread(treadName: String, block: () -> Unit) {
        thread(name = treadName, block = block)
    }
}