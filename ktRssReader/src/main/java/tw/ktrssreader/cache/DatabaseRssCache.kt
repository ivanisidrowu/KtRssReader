package tw.ktrssreader.cache

import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.constant.Const
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.persistence.db.entity.ChannelEntity
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.convertToByteArray
import tw.ktrssreader.utils.convertToObject
import tw.ktrssreader.utils.logD
import java.util.*

class DatabaseRssCache<T : RssStandardChannel> : RssCache<T> {

    private val logTag = this::class.java.simpleName
    private val db = KtRssProvider.provideDatabase(KtRssReaderGlobalConfig.getApplicationContext())
    private val dao = db.channelDao()

    override fun readCache(url: String, type: @Const.ChannelType Int, expiredTimeMillis: Long): T? {
        val entity = dao.getChannel(url, type) ?: return null

        val isCacheInvalid = Calendar.getInstance().timeInMillis > (entity.time + expiredTimeMillis)
        return if (isCacheInvalid) null else entity.channel.convertToObject() as? T
    }

    override fun saveCache(url: String, channel: RssStandardChannel) {
        logD(logTag, "[saveCache] url: $url")

        dao.insert(
            ChannelEntity(
                url = url,
                type = Const.ChannelType.convertChannelToType(channel),
                channel = channel.convertToByteArray(),
                time = Calendar.getInstance().timeInMillis
            )
        )
    }
}