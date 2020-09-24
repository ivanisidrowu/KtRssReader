package tw.ktrssreader.utils

import android.util.Log
import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.model.channel.AutoMixChannel
import tw.ktrssreader.model.channel.GoogleChannel
import tw.ktrssreader.model.channel.ITunesChannel
import tw.ktrssreader.model.channel.RssStandardChannel
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

@Suppress("UNCHECKED_CAST")
inline fun <reified T : RssStandardChannel, R> convertChannelTo(
    ifRssStandard: () -> Any,
    ifITunes: () -> Any,
    ifGoogle: () -> Any,
    ifAutoMix: () -> Any
): R {
    val clazz = T::class.java
    // Do NOT change this order
    return when {
        AutoMixChannel::class.java.isAssignableFrom(clazz) -> ifAutoMix()
        ITunesChannel::class.java.isAssignableFrom(clazz) -> ifITunes()
        GoogleChannel::class.java.isAssignableFrom(clazz) -> ifGoogle()
        else -> ifRssStandard()
    } as R
}