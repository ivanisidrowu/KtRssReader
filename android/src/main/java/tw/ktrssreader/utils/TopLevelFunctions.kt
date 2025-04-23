/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tw.ktrssreader.utils

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.kotlin.model.channel.AutoMixChannel
import tw.ktrssreader.kotlin.model.channel.GoogleChannel
import tw.ktrssreader.kotlin.model.channel.ITunesChannel
import tw.ktrssreader.kotlin.model.channel.RssStandardChannel

const val TAG_PREFIX = "KtRssReader/"

fun logD(tag: String, message: String) {
    if (KtRssReaderGlobalConfig.enableLog) Log.d("$TAG_PREFIX$tag", message)
}

fun logW(tag: String, message: String) {
    if (KtRssReaderGlobalConfig.enableLog) Log.w("$TAG_PREFIX$tag", message)
}

inline fun tryCatch(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
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
inline fun <reified T, R> convertChannelTo(
    ifRssStandard: () -> Any,
    ifITunes: () -> Any,
    ifGoogle: () -> Any,
    ifAutoMix: () -> Any,
): R? {
    val clazz = T::class.java
    // Do NOT change this order
    return when {
        AutoMixChannel::class.java.isAssignableFrom(clazz) -> ifAutoMix()
        ITunesChannel::class.java.isAssignableFrom(clazz) -> ifITunes()
        GoogleChannel::class.java.isAssignableFrom(clazz) -> ifGoogle()
        RssStandardChannel::class.java.isAssignableFrom(clazz) -> ifRssStandard()
        else -> null
    } as? R
}
