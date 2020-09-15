package tw.ktrssreader.utils

import android.os.Looper
import android.util.Log
import tw.ktrssreader.config.KtRssReaderGlobalConfig

const val TAG = "KtRssReader"

fun logD(message: String) {
    if (KtRssReaderGlobalConfig.enableLog) Log.d(TAG, message)
}

fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()