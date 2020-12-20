package tw.ktrssreader.reader

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.reader.strategy.*
import java.io.Serializable
import java.nio.charset.Charset

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