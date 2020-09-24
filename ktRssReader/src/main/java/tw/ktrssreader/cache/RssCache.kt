package tw.ktrssreader.cache

import tw.ktrssreader.constant.Const
import tw.ktrssreader.model.channel.RssStandardChannel

interface RssCache<out T : RssStandardChannel> {
    fun readCache(url: String, type: @Const.ChannelType Int, expiredTimeMillis: Long): T?
    fun saveCache(url: String, channel: RssStandardChannel)
}