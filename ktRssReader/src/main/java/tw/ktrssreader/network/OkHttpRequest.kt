package tw.ktrssreader.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class OkHttpRequest(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
    private val requestBuilder: Request.Builder = Request.Builder()
) {

    fun get(url: String): Flow<String> {
        return flow {
            val request = requestBuilder.url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val body =
                response.body?.string() ?: throw IOException("Failed to get the response body!")
            emit(body)
        }
    }
}