package tw.ktrssreader.utils

import android.os.Looper
import android.util.Log
import tw.ktrssreader.config.KtRssReaderGlobalConfig
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val TAG = "KtRssReader"

fun logD(message: String) {
    if (KtRssReaderGlobalConfig.enableLog) Log.d(TAG, message)
}

fun <T> T.convertToByteArray(): ByteArray {
    return ByteArrayOutputStream().use { bos ->
        val oos = ObjectOutputStream(bos)
        oos.writeObject(this)
        bos.toByteArray()
    }
}

fun <T> ByteArray.convertToObject(): T {
    @Suppress("UNCHECKED_CAST")
    return ByteArrayInputStream(this).use { bis ->
        val ois = ObjectInputStream(bis)
        ois.readObject() as T
    }
}