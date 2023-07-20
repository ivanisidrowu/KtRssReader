package tw.ktrssreader.reader.strategy

import kotlinx.coroutines.flow.Flow
import java.io.Serializable
import java.nio.charset.Charset

interface RssStrategy {
    fun read(rssText: String, useCache: Boolean, charset: Charset): Serializable

    suspend fun coRead(rssText: String, useCache: Boolean, charset: Charset): Serializable

    suspend fun flowRead(rssText: String, useCache: Boolean, charset: Charset): Flow<Serializable>
}
