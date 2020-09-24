package tw.ktrssreader.cache

import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.constant.Const
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.persistence.db.entity.ChannelEntity
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.convertToByteArray
import tw.ktrssreader.utils.convertToObject
import tw.ktrssreader.utils.logD

class DatabaseRssCache<T : RssStandardChannel> : RssCache<T> {

    private val logTag = this::class.java.simpleName
    private val db = KtRssProvider.provideDatabase(KtRssReaderGlobalConfig.getApplicationContext())
    private val dao = db.channelDao()

    override fun readCache(url: String, type: @Const.ChannelType Int): T? {
        return dao.getChannel(url, type)?.channel?.convertToObject() as? T
    }

    override fun saveCache(url: String, channel: RssStandardChannel) {
        logD(logTag, "[saveCache] url: $url")

        dao.insert(
            ChannelEntity(
                url = url,
                type = Const.ChannelType.convertChannelToType(channel),
                channel = channel.convertToByteArray(),
                time = System.currentTimeMillis()
            )
        )
    }
}