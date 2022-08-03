package tw.ktrssreader.reader.strategy

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.Reader
import tw.ktrssreader.kotlin.model.channel.ITunesChannelData
import java.io.Serializable
import java.nio.charset.Charset

class ITunesRssStrategy : RssStrategy {
    override fun read(rssText: String, useCache: Boolean, charset: Charset): Serializable =
        Reader.read<ITunesChannelData>(rssText) {
            this.useCache = useCache
            this.charset = charset
        }

    override suspend fun coRead(
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Serializable =
        Reader.coRead<ITunesChannelData>(rssText) {
            this.useCache = useCache
            this.charset = charset
        }

    override suspend fun flowRead(
        rssText: String,
        useCache: Boolean,
        charset: Charset
    ): Flow<Serializable> =
        Reader.flowRead<ITunesChannelData>(rssText) {
            this.useCache = useCache
            this.charset = charset
        }
}
