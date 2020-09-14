package tw.ktrssreader.provider

import tw.ktrssreader.fetcher.Fetcher
import tw.ktrssreader.fetcher.XmlFetcher

internal object KtRssProvider {
    fun provideXmlFetcher(): Fetcher = XmlFetcher()
}