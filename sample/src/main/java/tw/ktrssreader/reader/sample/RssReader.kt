package tw.ktrssreader.reader.sample

import java.io.Serializable
import java.nio.charset.Charset
import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.reader.sample.strategy.AutoMixStrategy
import tw.ktrssreader.reader.sample.strategy.CustomStrategy
import tw.ktrssreader.reader.sample.strategy.CustomWithOrderStrategy
import tw.ktrssreader.reader.sample.strategy.CustomWithRawDataStrategy
import tw.ktrssreader.reader.sample.strategy.GooglePlayRssStrategy
import tw.ktrssreader.reader.sample.strategy.ITunesRssStrategy
import tw.ktrssreader.reader.sample.strategy.RssStrategy
import tw.ktrssreader.reader.sample.strategy.StandardRssStrategy

object RssReader {
    private val strategies = hashMapOf<RssType, RssStrategy>().apply {
        put(RssType.Standard, StandardRssStrategy())
        put(RssType.ITunes, ITunesRssStrategy())
        put(RssType.GooglePlay, GooglePlayRssStrategy())
        put(RssType.AutoMix, AutoMixStrategy())
        put(RssType.Custom, CustomStrategy())
        put(RssType.CustomWithRawData, CustomWithRawDataStrategy())
        put(RssType.CustomWithOrder, CustomWithOrderStrategy())
    }

    private val strategy: (rssType: RssType) -> RssStrategy = {
        strategies.getOrElse(it) {
            error("Invalid strategy!")
        }
    }

    fun read(rssType: RssType, rssText: String, useCache: Boolean, charset: Charset): Serializable =
        strategy(rssType).read(rssText, useCache, charset)

    suspend fun coRead(
        rssType: RssType,
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Serializable =
        strategy(rssType).coRead(rssText, useCache, charset)

    suspend fun flowRead(
        rssType: RssType,
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Flow<Serializable> =
        strategy(rssType).flowRead(rssText, useCache, charset)
}
