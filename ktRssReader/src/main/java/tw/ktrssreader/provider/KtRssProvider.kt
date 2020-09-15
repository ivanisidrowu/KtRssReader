package tw.ktrssreader.provider

import tw.ktrssreader.fetcher.Fetcher
import tw.ktrssreader.fetcher.XmlFetcher
import tw.ktrssreader.model.channel.AutoMixChannel
import tw.ktrssreader.model.channel.GoogleChannel
import tw.ktrssreader.model.channel.ITunesChannel
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.parser.*

object KtRssProvider {

    fun provideXmlFetcher(): Fetcher = XmlFetcher()

    inline fun <reified T : RssStandardChannel> provideParser(): Parser<T> {
        @Suppress("UNCHECKED_CAST")
        return when (T::class) {
            ITunesChannel::class -> ITunesParser()
            GoogleChannel::class -> GoogleParser()
            AutoMixChannel::class -> AutoMixParser()
            else -> RssStandardParser()
        } as Parser<T>
    }
}