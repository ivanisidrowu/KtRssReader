package tw.ktrssreader.fetcher

import tw.ktrssreader.network.OkHttpRequest
import java.nio.charset.Charset

class XmlFetcher(private val okHttpRequest: OkHttpRequest = OkHttpRequest()) : Fetcher {

    override fun fetch(url: String, charset: Charset): String {
        return okHttpRequest.get(url = url, charset = charset)
    }
}