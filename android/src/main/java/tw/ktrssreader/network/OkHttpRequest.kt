/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
