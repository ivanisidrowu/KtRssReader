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
import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.constant.Const
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.ThreadUtils
import tw.ktrssreader.utils.logD
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias Config = KtRssReaderConfig.() -> Unit

object Reader {

    val logTag: String = this::class.java.simpleName

    @Throws(Exception::class)
    inline fun <reified T : RssStandardChannel> read(
        url: String,
        config: Config = {}
    ): T {
        check(!ThreadUtils.isMainThread()) { "Should not be called on main thread." }

        val ktRssReaderConfig = KtRssReaderConfig().apply(config)
        val charset = ktRssReaderConfig.charset
        val expiredTimeMillis = ktRssReaderConfig.expiredTimeMillis

        val rssCache = KtRssProvider.provideRssCache<T>()
        val channelType = Const.ChannelType.convertToChannelType<T>()
        val cacheChannel = if (ktRssReaderConfig.useRemote) {
            null
        } else {
            rssCache.readCache(url = url, type = channelType, expiredTimeMillis = expiredTimeMillis)
        }

        logD(
            tag = logTag,
            message = """
                            
            ┌───────────────────────────────────────────────
            │ url: $url
            │ channel: ${T::class.simpleName}
            │ charset: $charset
            │ expiredTimeMillis: $expiredTimeMillis
            └───────────────────────────────────────────────
            """
        )

        return if (cacheChannel == null) {
            logD(logTag, "[read] fetch remote data")
            val fetcher = KtRssProvider.provideXmlFetcher()
            val xml = fetcher.fetch(url = url, charset = charset)
            val parser = KtRssProvider.provideParser<T>()
            val channel = parser.parse(xml)
            ThreadUtils.runOnNewThread(treadName = "[read cache]") {
                rssCache.saveCache(url = url, channel = channel)
            }
            channel
        } else {
            logD(logTag, "[read] use local cache")
            cacheChannel
        }
    }

    @Throws(Exception::class)
    suspend inline fun <reified T : RssStandardChannel> coRead(
        url: String,
        crossinline config: Config = {}
    ) = suspendCoroutine<T> { it.resume(read(url = url, config = config)) }

    inline fun <reified T : RssStandardChannel> flowRead(
        url: String,
        crossinline config: Config = {}
    ) = flow<T> { emit(read(url = url, config = config)) }

    fun clearCache() {
        ThreadUtils.runOnNewThread("[clear cache]") {
            val db = KtRssProvider.provideDatabase(KtRssReaderGlobalConfig.getApplicationContext())
            db.channelDao().clearAll()
        }
    }
}