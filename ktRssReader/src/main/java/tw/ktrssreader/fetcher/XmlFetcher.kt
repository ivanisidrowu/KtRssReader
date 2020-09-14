package tw.ktrssreader.fetcher

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.network.OkHttpRequest

class XmlFetcher(private val okHttpRequest: OkHttpRequest = OkHttpRequest()) : Fetcher {
    override fun fetch(url: String): Flow<String> = okHttpRequest.get(url)
}