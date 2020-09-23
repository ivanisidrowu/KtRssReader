package tw.ktrssreader.provider

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import tw.ktrssreader.cache.DatabaseRssCache
import tw.ktrssreader.cache.RssCache
import tw.ktrssreader.fetcher.Fetcher
import tw.ktrssreader.fetcher.XmlFetcher
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.network.OkHttpRequest
import tw.ktrssreader.parser.*
import tw.ktrssreader.persistence.db.KtRssReaderDatabase
import tw.ktrssreader.utils.convertChannelTo

object KtRssProvider {

    fun provideDatabase(applicationContext: Context) =
        KtRssReaderDatabase.getInstance(applicationContext)

    fun provideXmlFetcher(): Fetcher = XmlFetcher()

    fun <T : RssStandardChannel> provideRssCache(): RssCache<T> = DatabaseRssCache()

    fun providerOkHttpRequest(
        okHttpClient: OkHttpClient = OkHttpClient(),
        requestBuilder: Request.Builder = Request.Builder()
    ): OkHttpRequest {
        return OkHttpRequest(okHttpClient = okHttpClient, requestBuilder = requestBuilder)
    }

    inline fun <reified T : RssStandardChannel> provideParser(): Parser<T> {
        return convertChannelTo<T, Parser<T>>(
            ifITunes = { ITunesParser() },
            ifGoogle = { GoogleParser() },
            ifAutoMix = { AutoMixParser() },
            ifRssStandard = { RssStandardParser() }
        )
    }
}