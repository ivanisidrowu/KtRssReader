package tw.ktrssreader.reader.sample.strategy

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.generated.RssRawDataReader
import java.io.Serializable
import java.nio.charset.Charset

class CustomWithRawDataStrategy : RssStrategy {
    override fun read(rssText: String, useCache: Boolean, charset: Charset): Serializable =
        RssRawDataReader.read(rssText) {
            this.useCache = useCache
            this.charset = charset
        }

    override suspend fun coRead(
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Serializable =
        RssRawDataReader.coRead(rssText) {
            this.useCache = useCache
            this.charset = charset
        }

    override suspend fun flowRead(
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Flow<Serializable> =
        RssRawDataReader.flowRead(rssText) {
            this.useCache = useCache
            this.charset = charset
        }
}
