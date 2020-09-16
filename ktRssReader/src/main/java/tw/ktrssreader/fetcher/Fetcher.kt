package tw.ktrssreader.fetcher

import java.nio.charset.Charset

interface Fetcher {
    fun fetch(url: String, charset: Charset?): String
}