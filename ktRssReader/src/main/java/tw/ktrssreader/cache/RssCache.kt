package tw.ktrssreader.cache

import tw.ktrssreader.model.channel.RssStandardChannel

interface RssCache<out T : RssStandardChannel> {
    fun readCache(url: String): T?
    fun saveCache(url: String, channel: RssStandardChannel)
}