package tw.ktrssreader.reader.sample.strategy

import java.io.Serializable
import java.nio.charset.Charset
import kotlinx.coroutines.flow.Flow

interface RssStrategy {
    fun read(rssText: String, useCache: Boolean, charset: Charset): Serializable

    suspend fun coRead(rssText: String, useCache: Boolean, charset: Charset): Serializable

    suspend fun flowRead(rssText: String, useCache: Boolean, charset: Charset): Flow<Serializable>
}
