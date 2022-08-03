package tw.ktrssreader

import tw.ktrssreader.annotation.OrderType
import tw.ktrssreader.annotation.RssTag
import java.io.Serializable

@RssTag(name = "channel")
data class RssOrderData(
    @RssTag(order = [OrderType.GOOGLE, OrderType.RSS_STANDARD, OrderType.ITUNES])
    val title: String?,
    @RssTag(
        name = "item",
        order = [OrderType.ITUNES, OrderType.GOOGLE, OrderType.RSS_STANDARD]
    )
    val list: List<RssOrderItem>
) : Serializable

@RssTag(name = "item", order = [OrderType.RSS_STANDARD, OrderType.GOOGLE, OrderType.ITUNES])
data class RssOrderItem(
    val title: String?,
    @RssTag(order = [OrderType.GOOGLE])
    val author: String?,
) : Serializable
