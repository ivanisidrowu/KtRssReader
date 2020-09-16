package tw.ktrssreader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.nio.charset.Charset

class OkHttpRequest(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
    private val requestBuilder: Request.Builder = Request.Builder()
) {

    fun get(url: String, charset: Charset): String {
        val request = requestBuilder.url(url).build()
        val response = okHttpClient.newCall(request).execute()
        return response.body?.source()?.readString(charset)
            ?: throw IOException("Failed to get the response body!")
    }
}