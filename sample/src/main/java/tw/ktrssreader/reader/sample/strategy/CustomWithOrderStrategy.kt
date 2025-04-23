package tw.ktrssreader.reader.sample.strategy

import java.io.Serializable
import java.nio.charset.Charset
import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.generated.RssOrderDataReader

class CustomWithOrderStrategy : RssStrategy {
    override fun read(rssText: String, useCache: Boolean, charset: Charset): Serializable =
        RssOrderDataReader.read(rssText) {
            this.useCache
            this.charset
        }

    override suspend fun coRead(
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Serializable =
        RssOrderDataReader.coRead(rssText) {
            this.useCache
            this.charset
        }

    override suspend fun flowRead(
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Flow<Serializable> =
        RssOrderDataReader.flowRead(rssText) {
            this.useCache
            this.charset
        }
}
