package tw.ktrssreader.cache

import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.persistence.db.entity.ChannelEntity
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.convertToByteArray
import tw.ktrssreader.utils.convertToObject
import tw.ktrssreader.utils.logD

class DatabaseRssCache<T : RssStandardChannel> : RssCache<T> {

    private val db = KtRssProvider.provideDatabase(KtRssReaderGlobalConfig.getApplicationContext())
    private val dao = db.channelDao()

    override fun readCache(url: String): T? {
        return dao.getChannelByUrl(url)?.channel?.convertToObject() as? T
    }

    override fun saveCache(url: String, channel: RssStandardChannel) {
        logD("[saveCache] url: $url")

        dao.insert(
            ChannelEntity(
                url = url,
                channel = channel.convertToByteArray(),
                time = System.currentTimeMillis()
            )
        )
    }
}