package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.Cloud
import tw.ktrssreader.model.channel.Image
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.model.channel.TextInput
import tw.ktrssreader.model.item.*

object ChannelItemTestData {
    // item attributes
    private const val ITEM_TITLE_ATTR = "item title - Full"
    private const val ITEM_TITLE_ATTR_PARTIAL = "item title - Partial"
    private val ITEM_GUID_ATTR_WITH_IS_PERMA_LINK = Guid(value = "http://item.guid", isPermaLink = true)
    private val ITEM_GUID_ATTR = Guid(value = "http://item.guid", isPermaLink = null)
    private const val ITEM_DESCRIPTION_ATTR = "item description How do Americans get ready to work with Russians aboard the International Space Station? They take a crash course in culture, language and protocol at Russia's <a href=\"http://howe.iki.rssi.ru/GCTC/gctc_e.htm\">Star City</a>."
    private const val ITEM_DESCRIPTION_ATTR_2 = "item description 2"
    private const val ITEM_PUB_DATE_ATTR = "Fri, 30 May 2003 11:06:42 GMT"
    private const val ITEM_LINK_ATTR = "http://item.link"
    private const val ITEM_AUTHOR_ATTR = "item.author@example.com"
    private val ITEM_CATEGORY_ATTR = Category(name = "item category 1", domain = "http://item.category.domain")
    private val ITEM_CATEGORY_ATTR_WITHOUT_DOMAIN = Category(name = "item category 2", domain = null)
    private const val ITEM_COMMENTS_ATTR = "http://item.comment"
    private val ITEM_SOURCE_ATTR = Source(title = "item source", url = "item.source.url")
    private val ITEM_SOURCE_ATTR_WITHOUT_URL = Source(title = "item source", url = null)
    private val ITEM_ENCLOSURE_ATTR = Enclosure(
        url = "http://item.enclosure.url/item.mp3",
        length = 24986239,
        type = "audio/mpeg"
    )
    private val ITEM_PARTIAL_ENCLOSURE_ATTR = Enclosure(
        url = null,
        length = null,
        type = "audio/mpeg"
    )

    // channel attributes
    private const val CHANNEL_TITLE_ATTR = "channel title"
    private const val CHANNEL_DESCRIPTION_ATTR = "channel description"
    private val CHANNEL_IMAGE_ATTR = Image(
        link = "http://channel.image.link",
        title = "channel image title",
        url = "http://channel.image.url",
        description = "channel image description",
        height = 32,
        width = 96
    )
    private val CHANNEL_PARTIAL_IMAGE_ATTR = Image(
        link = "http://channel.image.link",
        title = "channel image title",
        width = 96,
        url = null,
        description = null,
        height = null,
    )
    private const val CHANNEL_LANGUAGE_ATTR = "en-us"
    private val CHANNEL_CATEGORY_ATTR_WITHOUT_DOMAIN = Category(name = "channel category 1", domain = null)
    private val CHANNEL_CATEGORY_ATTR = Category(name = "channel category 2", domain = "http://channel.category.domain")
    private const val CHANNEL_LINK_ATTR = "http://channel.link"
    private const val CHANNEL_COPYRIGHT_ATTR = "channel copyright"
    private const val CHANNEL_MANAGING_EDITOR_ATTR = "managingEditor@example.com"
    private const val CHANNEL_WEBMASTER_ATTR = "webmaster@example.com"
    private const val CHANNEL_PUB_DATE_ATTR = "Tue, 10 Jun 2003 04:00:00 GMT"
    private const val CHANNEL_LAST_BUILD_DATE_ATTR = "Tue, 10 Jun 2003 09:41:01 GMT"
    private const val CHANNEL_GENERATOR_ATTR = "channel generator"
    private const val CHANNEL_DOCS_ATTR = "http://channel.docs"
    private val CHANNEL_CLOULD_ATTR = Cloud(
        domain = "liftoff.msfc.nasa.gov",
        port = 80,
        path = "/RPC2",
        registerProcedure = "myCloud.rssPleaseNotify",
        protocol = "xml-rpc"
    )

    private val CHANNEL_PARTIAL_CLOULD_ATTR = Cloud(
        domain = null,
        port = null,
        path = "/RPC2",
        registerProcedure = "myCloud.rssPleaseNotify",
        protocol = "xml-rpc"
    )
    private const val CHANNEL_TTL_ATTR = 60
    private const val CHANNEL_RATING_ATTR = "(PICS-1.1 \"http://channel.rating\" l by \"webmaster@example.com\" on \"2007.01.29T10:09-0800\" r (n 0 s 0 v 0 l 0))"
    private val CHANNEL_TEXT_INPUT_ATTR = TextInput(
        title = "channel textInput title",
        description = "channel textInput description",
        name = "channel textInput name",
        link = "http://channel.textInput.link"
    )
    private val CHANNEL_PARTIAL_TEXT_INPUT_ATTR = TextInput(
        title = "channel textInput title",
        description = "channel textInput description",
        name = null,
        link = "http://channel.textInput.link"
    )
    private val CHANNEL_SKIP_HOURS_ATTR = listOf(0, 1, 2, 22, 23)
    private val CHANNEL_SKIP_DAYS_ATTR = listOf("Saturday", "Sunday")


    private val ITEM_FULL_RSS_ATTRS = RssStandardItem(
        title = ITEM_TITLE_ATTR,
        enclosure = ITEM_ENCLOSURE_ATTR,
        guid = ITEM_GUID_ATTR,
        pubDate = ITEM_PUB_DATE_ATTR,
        description = ITEM_DESCRIPTION_ATTR,
        link = ITEM_LINK_ATTR,
        author = ITEM_AUTHOR_ATTR,
        categories = listOf(
            ITEM_CATEGORY_ATTR,
            ITEM_CATEGORY_ATTR_WITHOUT_DOMAIN
        ),
        comments = ITEM_COMMENTS_ATTR,
        source = ITEM_SOURCE_ATTR
    )

    private val ITEM_RSS_PARTIAL_1 = RssStandardItem(
        title = ITEM_TITLE_ATTR_PARTIAL,
        guid = ITEM_GUID_ATTR_WITH_IS_PERMA_LINK,
        pubDate = ITEM_PUB_DATE_ATTR,
        description = ITEM_DESCRIPTION_ATTR_2,
        enclosure = null,
        link = null,
        author = null,
        categories = null,
        comments = null,
        source = ITEM_SOURCE_ATTR_WITHOUT_URL,
    )

    private val ITEM_RSS_PARTIAL_2 = RssStandardItem(
        title = null,
        guid = null,
        pubDate = null,
        description = null,
        enclosure = ITEM_PARTIAL_ENCLOSURE_ATTR,
        link = null,
        author = null,
        categories = null,
        comments = ITEM_COMMENTS_ATTR,
        source = null,
    )


    val FULL_RSS_CHANNEL = RssStandardChannel(
        title = CHANNEL_TITLE_ATTR,
        description = CHANNEL_DESCRIPTION_ATTR,
        image = CHANNEL_IMAGE_ATTR,
        language = CHANNEL_LANGUAGE_ATTR,
        categories = listOf(
            CHANNEL_CATEGORY_ATTR_WITHOUT_DOMAIN,
            CHANNEL_CATEGORY_ATTR
        ),
        link = CHANNEL_LINK_ATTR,
        copyright = CHANNEL_COPYRIGHT_ATTR,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = CHANNEL_CLOULD_ATTR,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = CHANNEL_TEXT_INPUT_ATTR,
        skipHours = CHANNEL_SKIP_HOURS_ATTR,
        skipDays = CHANNEL_SKIP_DAYS_ATTR,
        items = listOf(ITEM_FULL_RSS_ATTRS, ITEM_RSS_PARTIAL_1, ITEM_RSS_PARTIAL_2)
    )

    val PARTIAL_RSS_CHANNEL = RssStandardChannel(
        title = CHANNEL_TITLE_ATTR,
        description = null,
        image = null,
        language = CHANNEL_LANGUAGE_ATTR,
        categories = null,
        link = CHANNEL_LINK_ATTR,
        copyright = null,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = null,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = null,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )

    val RSS_CHANNEL_PARTIAL_IMAGE = RssStandardChannel(
        title = CHANNEL_TITLE_ATTR,
        description = null,
        image = CHANNEL_PARTIAL_IMAGE_ATTR,
        language = CHANNEL_LANGUAGE_ATTR,
        categories = null,
        link = CHANNEL_LINK_ATTR,
        copyright = null,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = null,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = null,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )

    val RSS_CHANNEL_PARTIAL_TEXT_IMAGE = RssStandardChannel(
        title = CHANNEL_TITLE_ATTR,
        description = null,
        image = null,
        language = CHANNEL_LANGUAGE_ATTR,
        categories = null,
        link = CHANNEL_LINK_ATTR,
        copyright = null,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = null,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = CHANNEL_PARTIAL_TEXT_INPUT_ATTR,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )

    val RSS_CHANNEL_PARTIAL_CLOUD = RssStandardChannel(
        title = CHANNEL_TITLE_ATTR,
        description = null,
        image = null,
        language = CHANNEL_LANGUAGE_ATTR,
        categories = null,
        link = CHANNEL_LINK_ATTR,
        copyright = null,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = CHANNEL_PARTIAL_CLOULD_ATTR,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = null,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )
}