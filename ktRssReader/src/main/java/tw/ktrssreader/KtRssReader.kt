package tw.ktrssreader

import tw.ktrssreader.fetcher.Fetcher
import tw.ktrssreader.provider.KtRssProvider

class KtRssReader {
    private val fetcher: Fetcher = KtRssProvider.provideXmlFetcher()
}