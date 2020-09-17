package tw.ktrssreader.fetcher

import tw.ktrssreader.provider.KtRssProvider
import java.nio.charset.Charset

class XmlFetcher : Fetcher {

    override fun fetch(url: String, charset: Charset?): String {
        val okHttpRequest = KtRssProvider.providerOkHttpRequest()
        return okHttpRequest.get(url = url, charset = charset)
    }
}