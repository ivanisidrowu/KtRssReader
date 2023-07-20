package tw.ktrssreader

import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag
import java.io.Serializable

@RssTag(name = "channel")
data class RssRawData(
    @RssRawData(["itunes:summary", "googleplay:description", "description"])
    val description: String?,
    @RssTag(name = "item")
    val list: List<RssRawItem>
) : Serializable

@RssTag(name = "item")
data class RssRawItem(
    @RssRawData(["googleplay:author", "author", "itunes:author"])
    val author: String?
) : Serializable
