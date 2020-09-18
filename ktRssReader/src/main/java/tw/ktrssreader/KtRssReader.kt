package tw.ktrssreader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tw.ktrssreader.config.KtRssReaderConfig
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.ThreadUtils
import tw.ktrssreader.utils.logD
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

typealias Config = KtRssReaderConfig.() -> Unit

@Throws(Exception::class)
inline fun <reified T : RssStandardChannel> reader(
    url: String,
    config: Config = {}
): T {
    check(!ThreadUtils.isMainThread()) { "Should not be called on main thread." }

    val ktRssReaderConfig = KtRssReaderConfig().apply(config)
    val rssCache = KtRssProvider.provideRssCache<T>()
    val cacheChannel = if (ktRssReaderConfig.useRemote) null else rssCache.readCache(url)
    return if (cacheChannel == null) {
        logD("[reader] fetch remote data")
        val fetcher = KtRssProvider.provideXmlFetcher()
        val xml = fetcher.fetch(url = url, charset = ktRssReaderConfig.charset)
        val parser = KtRssProvider.provideParser<T>()
        val channel = parser.parse(xml)
        ThreadUtils.runOnNewThread(treadName = "[reader cache]") {
            rssCache.saveCache(url = url, channel = channel)
        }
        channel
    } else {
        logD("[reader] use local cache")
        cacheChannel
    }
}

@Throws(Exception::class)
suspend inline fun <reified T : RssStandardChannel> readerSuspend(
    url: String,
    crossinline config: Config = {}
): T = suspendCoroutine { it.resume(reader(url = url, config = config)) }

inline fun <reified T : RssStandardChannel> readerFlow(
    url: String,
    crossinline config: Config = {}
): Flow<T> = flow<T> { emit(reader(url = url, config = config)) }