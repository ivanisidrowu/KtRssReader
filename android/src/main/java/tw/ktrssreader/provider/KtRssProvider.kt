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

package tw.ktrssreader.provider

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import tw.ktrssreader.cache.DatabaseRssCache
import tw.ktrssreader.cache.RssCache
import tw.ktrssreader.fetcher.Fetcher
import tw.ktrssreader.fetcher.XmlFetcher
import tw.ktrssreader.network.OkHttpRequest
import tw.ktrssreader.parser.*
import tw.ktrssreader.persistence.db.KtRssReaderDatabase
import tw.ktrssreader.utils.convertChannelTo

object KtRssProvider {

    fun provideDatabase(applicationContext: Context) =
        KtRssReaderDatabase.getInstance(applicationContext)

    fun provideXmlFetcher(): Fetcher = XmlFetcher()

    fun <T> provideRssCache(): RssCache<T> = DatabaseRssCache()

    fun providerOkHttpRequest(
        okHttpClient: OkHttpClient = OkHttpClient(),
        requestBuilder: Request.Builder = Request.Builder()
    ): OkHttpRequest {
        return OkHttpRequest(okHttpClient = okHttpClient, requestBuilder = requestBuilder)
    }

    inline fun <reified T> provideParser(): Parser<T>? {
        return convertChannelTo<T, Parser<T>>(
            ifRssStandard = { RssStandardParser() },
            ifITunes = { ITunesParser() },
            ifGoogle = { GoogleParser() },
            ifAutoMix = { AutoMixParser() }
        )
    }
}