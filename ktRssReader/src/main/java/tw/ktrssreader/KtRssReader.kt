package tw.ktrssreader

import tw.ktrssreader.fetcher.Fetcher
import tw.ktrssreader.fetcher.XmlFetcher

class KtRssReader(private val fetcher: Fetcher = XmlFetcher()) {
}