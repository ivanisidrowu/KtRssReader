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

package tw.ktrssreader

import kotlinx.coroutines.flow.flow
import tw.ktrssreader.config.KtRssReaderConfig
import tw.ktrssreader.constant.Const
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.ThreadUtils
import tw.ktrssreader.utils.logD
import tw.ktrssreader.utils.tryCatch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias Config = KtRssReaderConfig.() -> Unit

object Reader {

    val logTag: String = this::class.java.simpleName

    @Throws(Exception::class)
    inline fun <reified T> read(
        url: String,
        customParser: ((String) -> T?) = { null },
        config: Config = {},
    ): T {
        check(!ThreadUtils.isMainThread()) { "Should not be called on main thread." }

        val ktRssReaderConfig = KtRssReaderConfig().apply(config)
        val charset = ktRssReaderConfig.charset
        val useCache = ktRssReaderConfig.useCache
        val flushCache = ktRssReaderConfig.flushCache
        val expiredTimeMillis = ktRssReaderConfig.expiredTimeMillis

        val rssCache = KtRssProvider.provideRssCache<T>()
        val channelType = Const.ChannelType.convertToChannelType<T>()
        val cacheChannel = if (useCache) {
            rssCache.readCache(url = url, type = channelType, expiredTimeMillis = expiredTimeMillis)
        } else {
            null
        }

        logD(
            tag = logTag,
            message = """
                            
            ┌───────────────────────────────────────────────
            │ url: $url
            │ channel: ${T::class.simpleName}
            │ charset: $charset
            │ useCache: $useCache
            │ flushCache: $flushCache
            │ expiredTimeMillis: $expiredTimeMillis
            └───────────────────────────────────────────────
            """
        )

        if (flushCache) {
            tryCatch { rssCache.removeCache(url) }
        }

        return if (cacheChannel == null) {
            logD(logTag, "[read] fetch remote data")
            val fetcher = KtRssProvider.provideXmlFetcher()
            val xml = fetcher.fetch(url = url, charset = charset)
            val parser = KtRssProvider.provideParser<T>()
            val channel = parser?.parse(xml)
                ?: customParser(xml)
                ?: throw IllegalArgumentException("There is no way to parse ${T::class.java}!")

            if (useCache) {
                tryCatch { rssCache.saveCache(url = url, channel = channel) }
            }
            channel
        } else {
            logD(logTag, "[read] use local cache")
            cacheChannel
        }
    }

    @Throws(Exception::class)
    suspend inline fun <reified T> coRead(
        url: String,
        crossinline customParser: ((String) -> T?) = { null },
        crossinline config: Config = {}
    ) = suspendCoroutine<T> {
        it.resume(read(url = url, customParser = customParser, config = config))
    }

    inline fun <reified T> flowRead(
        url: String,
        crossinline customParser: ((String) -> T?) = { null },
        crossinline config: Config = {}
    ) = flow<T> { emit(read(url = url, customParser = customParser, config = config)) }

    fun clearCache() {
        ThreadUtils.runOnNewThread("[clear cache]") {
            val db = KtRssProvider.provideDatabase(KtRssReaderInitializer.applicationContext)
            db.channelDao().clearAll()
        }
    }
}
