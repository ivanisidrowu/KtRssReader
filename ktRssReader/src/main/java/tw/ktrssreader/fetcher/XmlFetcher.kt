package tw.ktrssreader.fetcher

import tw.ktrssreader.network.OkHttpRequest

class XmlFetcher(private val okHttpRequest: OkHttpRequest = OkHttpRequest()) : Fetcher {
    override fun fetch(url: String) = okHttpRequest.get(url)
}