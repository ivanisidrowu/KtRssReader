package tw.ktrssreader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.nio.charset.Charset

class OkHttpRequest(
    private val okHttpClient: OkHttpClient,
    private val requestBuilder: Request.Builder
) {

    fun get(url: String, charset: Charset?): String {
        val request = requestBuilder.url(url).build()
        val response = okHttpClient.newCall(request).execute()
        val body = response.body
        val responseString = if (charset == null) {
            body?.string()
        } else {
            body?.source()?.use { it.readString(charset) }
        }
        return responseString ?: throw IOException("Failed to get the response body!")
    }
}