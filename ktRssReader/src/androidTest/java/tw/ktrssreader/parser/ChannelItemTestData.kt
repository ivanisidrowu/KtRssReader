package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.*
import tw.ktrssreader.model.item.*

object ChannelItemTestData {
    const val RSS_FOLDER = "RSS"
    const val GOOGLE_PLAY_FOLDER = "GooglePlay"
    const val ITUNES_FOLDER = "iTunes"
    const val MISMATCH_TAG_FOLDER = "StartOrEndTagMismatch"

    class TestRssStandardItem(
        override val title: String? = ITEM_TITLE_ATTR,
        override val enclosure: Enclosure? = ITEM_ENCLOSURE_ATTR,
        override val guid: Guid? = ITEM_GUID_ATTR,
        override val pubDate: String? = ITEM_PUB_DATE_ATTR,
        override val description: String? = ITEM_DESCRIPTION_ATTR,
        override val link: String? = ITEM_LINK_ATTR,
        override val author: String? = ITEM_AUTHOR_ATTR,
        override val categories: List<Category>? = listOf(
            ITEM_CATEGORY_ATTR,
            ITEM_CATEGORY_ATTR_WITHOUT_DOMAIN
        ),
        override val comments: String? = ITEM_COMMENTS_ATTR,
        override val source: Source? = ITEM_SOURCE_ATTR
    ) : RssStandardItem(
        title = title,
        enclosure = enclosure,
        guid = guid,
        pubDate = pubDate,
        description = description,
        link = link,
        author = author,
        categories = categories,
        comments = comments,
        source = source,
    )

    class TestRssStandardChannel(
        override val title: String? = CHANNEL_TITLE_ATTR,
        override val description: String? = CHANNEL_DESCRIPTION_ATTR,
        override val image: Image? = CHANNEL_IMAGE_ATTR,
        override val language: String? = CHANNEL_LANGUAGE_ATTR,
        override val categories: List<Category>? = listOf(
            CHANNEL_CATEGORY_ATTR_WITHOUT_DOMAIN,
            CHANNEL_CATEGORY_ATTR
        ),
        override val link: String? = CHANNEL_LINK_ATTR,
        override val copyright: String? = CHANNEL_COPYRIGHT_ATTR,
        override val managingEditor: String? = CHANNEL_MANAGING_EDITOR_ATTR,
        override val webMaster: String? = CHANNEL_WEBMASTER_ATTR,
        override val pubDate: String? = CHANNEL_PUB_DATE_ATTR,
        override val lastBuildDate: String? = CHANNEL_LAST_BUILD_DATE_ATTR,
        override val generator: String? = CHANNEL_GENERATOR_ATTR,
        override val docs: String? = CHANNEL_DOCS_ATTR,
        override val cloud: Cloud? = CHANNEL_CLOUD_ATTR,
        override val ttl: Int? = CHANNEL_TTL_ATTR,
        override val rating: String? = CHANNEL_RATING_ATTR,
        override val textInput: TextInput? = CHANNEL_TEXT_INPUT_ATTR,
        override val skipHours: List<Int>? = CHANNEL_SKIP_HOURS_ATTR,
        override val skipDays: List<String>? = CHANNEL_SKIP_DAYS_ATTR,
        override val items: List<RssStandardItem>? = listOf(ITEM_FULL_RSS_ATTRS, ITEM_RSS_PARTIAL_1, ITEM_RSS_PARTIAL_2),
    ): RssStandardChannel(
        title = title,
        description = description,
        image = image,
        language = language,
        categories = categories,
        link = link,
        copyright = copyright,
        managingEditor = managingEditor,
        webMaster = webMaster,
        pubDate = pubDate,
        lastBuildDate = lastBuildDate,
        generator = generator,
        docs = docs,
        cloud = cloud,
        ttl = ttl,
        rating = rating,
        textInput = textInput,
        skipHours = skipHours,
        skipDays = skipDays,
        items = items
    )

    // item attributes
    private const val ITEM_TITLE_ATTR = "item title - Full"
    private const val ITEM_TITLE_ATTR_PARTIAL = "item title - Partial"
    private val ITEM_GUID_ATTR_WITH_IS_PERMA_LINK =
        Guid(value = "http://item.guid", isPermaLink = true)
    private val ITEM_GUID_ATTR = Guid(value = "http://item.guid", isPermaLink = null)
    private const val ITEM_DESCRIPTION_ATTR =
        "item description <a href=\"http://item.description\">item description</a>."
    private const val ITEM_DESCRIPTION_ATTR_2 = "item description 2"
    private const val ITEM_PUB_DATE_ATTR = "Fri, 30 May 2003 11:06:42 GMT"
    private const val ITEM_LINK_ATTR = "http://item.link"
    private const val ITEM_AUTHOR_ATTR = "item.author@example.com"
    private val ITEM_CATEGORY_ATTR =
        Category(name = "item category 1", domain = "http://item.category.domain")
    private val ITEM_CATEGORY_ATTR_WITHOUT_DOMAIN =
        Category(name = "item category 2", domain = null)
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
    private val CHANNEL_CATEGORY_ATTR_WITHOUT_DOMAIN =
        Category(name = "channel category 1", domain = null)
    private val CHANNEL_CATEGORY_ATTR =
        Category(name = "channel category 2", domain = "http://channel.category.domain")
    private const val CHANNEL_LINK_ATTR = "http://channel.link"
    private const val CHANNEL_COPYRIGHT_ATTR = "channel copyright"
    private const val CHANNEL_MANAGING_EDITOR_ATTR = "managingEditor@example.com"
    private const val CHANNEL_WEBMASTER_ATTR = "webmaster@example.com"
    private const val CHANNEL_PUB_DATE_ATTR = "Tue, 10 Jun 2003 04:00:00 GMT"
    private const val CHANNEL_LAST_BUILD_DATE_ATTR = "Tue, 10 Jun 2003 09:41:01 GMT"
    private const val CHANNEL_GENERATOR_ATTR = "channel generator"
    private const val CHANNEL_DOCS_ATTR = "http://channel.docs"
    private val CHANNEL_CLOUD_ATTR = Cloud(
        domain = "channel.cloud.domain",
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
    private const val CHANNEL_RATING_ATTR =
        "(PICS-1.1 \"http://channel.rating\" l by \"webmaster@example.com\" on \"2007.01.29T10:09-0800\" r (n 0 s 0 v 0 l 0))"
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


    private val ITEM_FULL_RSS_ATTRS = TestRssStandardItem()

    private val ITEM_RSS_PARTIAL_1 = TestRssStandardItem(
        title = ITEM_TITLE_ATTR_PARTIAL,
        guid = ITEM_GUID_ATTR_WITH_IS_PERMA_LINK,
        description = ITEM_DESCRIPTION_ATTR_2,
        enclosure = null,
        link = null,
        author = null,
        categories = null,
        comments = null,
        source = ITEM_SOURCE_ATTR_WITHOUT_URL
    )

    private val ITEM_RSS_PARTIAL_2 = TestRssStandardItem(
        title = null,
        guid = null,
        pubDate = null,
        description = null,
        enclosure = ITEM_PARTIAL_ENCLOSURE_ATTR,
        link = null,
        author = null,
        categories = null,
        source = null,
    )

    val FULL_RSS_CHANNEL = TestRssStandardChannel()

    val PARTIAL_RSS_CHANNEL = TestRssStandardChannel(
        description = null,
        image = null,
        categories = null,
        copyright = null,
        cloud = null,
        textInput = null,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )

    val RSS_CHANNEL_PARTIAL_IMAGE = TestRssStandardChannel(
        description = null,
        image = CHANNEL_PARTIAL_IMAGE_ATTR,
        categories = null,
        copyright = null,
        cloud = null,
        textInput = null,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )

    val RSS_CHANNEL_PARTIAL_TEXT_IMAGE = TestRssStandardChannel(
        description = null,
        image = null,
        categories = null,
        copyright = null,
        cloud = null,
        textInput = CHANNEL_PARTIAL_TEXT_INPUT_ATTR,
        skipHours = null,
        skipDays = null,
        items = null
    )

    val RSS_CHANNEL_PARTIAL_CLOUD = TestRssStandardChannel(
        description = null,
        image = null,
        categories = null,
        copyright = null,
        cloud = CHANNEL_PARTIAL_CLOULD_ATTR,
        textInput = null,
        skipHours = null,
        skipDays = null,
        items = listOf(ITEM_FULL_RSS_ATTRS)
    )

    fun TestRssStandardChannel.toRssStandardChannel(): RssStandardChannel {
        return RssStandardChannel(
            title = title,
            description = description,
            image = image,
            language = language,
            categories = categories,
            link = link,
            copyright = copyright,
            managingEditor = managingEditor,
            webMaster = webMaster,
            pubDate = pubDate,
            lastBuildDate = lastBuildDate,
            generator = generator,
            docs = docs,
            cloud = cloud,
            ttl = ttl,
            rating = rating,
            textInput = textInput,
            skipHours = skipHours,
            skipDays = skipDays,
            items = items
        )
    }


    // Google play test data
    private const val GOOGLE_CHANNEL_AUTHOR_ATTR = "channel author - google play"
    private const val GOOGLE_CHANNEL_IMAGE_ATTR = "http://channel.image.url"
    private const val GOOGLE_CHANNEL_EMAIL_ATTR = "channel.email.googleplay@example.com"
    private const val GOOGLE_CHANNEL_OWNER_ATTR = "channel.owner.googleplay@example.com"
    private const val GOOGLE_CHANNEL_CATEGORY_ATTR = "Games & Hobbies"
    private const val GOOGLE_CHANNEL_DESCRIPTION_ATTR = "channel description - google play"
    private const val GOOGLE_CHANNEL_LANGUAGE_ATTR = "en"

    private const val GOOGLE_ITEM_DESCRIPTION_ATTR = "item description - google play"
    private val FULL_GOOGLE_ITEM = GoogleItem(
        title = ITEM_TITLE_ATTR,
        enclosure = ITEM_ENCLOSURE_ATTR,
        guid = ITEM_GUID_ATTR,
        pubDate = ITEM_PUB_DATE_ATTR,
        description = GOOGLE_ITEM_DESCRIPTION_ATTR,
        link = ITEM_LINK_ATTR,
        author = ITEM_AUTHOR_ATTR,
        categories = listOf(
            ITEM_CATEGORY_ATTR,
            ITEM_CATEGORY_ATTR_WITHOUT_DOMAIN
        ),
        comments = ITEM_COMMENTS_ATTR,
        source = ITEM_SOURCE_ATTR,
        explicit = true,
        block = true
    )

    private val PARTIAL_GOOGLE_ITEM_1 = FULL_GOOGLE_ITEM.copy(
        title = ITEM_TITLE_ATTR_PARTIAL,
        enclosure = null,
        guid = ITEM_GUID_ATTR_WITH_IS_PERMA_LINK,
        pubDate = ITEM_PUB_DATE_ATTR,
        link = null,
        author = null,
        categories = null,
        comments = null,
        source = ITEM_SOURCE_ATTR_WITHOUT_URL,
        explicit = false,
        block = false
    )

    private val PARTIAL_GOOGLE_ITEM_2 = FULL_GOOGLE_ITEM.copy(
        title = null,
        enclosure = ITEM_PARTIAL_ENCLOSURE_ATTR,
        guid = null,
        pubDate = null,
        description = null,
        link = null,
        author = null,
        categories = null,
        source = null,
        explicit = null,
        block = null

    )
    val FULL_GOOGLE_CHANNEL = GoogleChannel(
        title = CHANNEL_TITLE_ATTR,
        description = GOOGLE_CHANNEL_DESCRIPTION_ATTR,
        image = Image(
            link = null,
            title = null,
            url = GOOGLE_CHANNEL_IMAGE_ATTR,
            description = null,
            height = null,
            width = null
        ),
        language = GOOGLE_CHANNEL_LANGUAGE_ATTR,
        categories = listOf(Category(name = GOOGLE_CHANNEL_CATEGORY_ATTR, domain = null)),
        link = CHANNEL_LINK_ATTR,
        copyright = CHANNEL_COPYRIGHT_ATTR,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = CHANNEL_CLOUD_ATTR,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = CHANNEL_TEXT_INPUT_ATTR,
        skipHours = CHANNEL_SKIP_HOURS_ATTR,
        skipDays = CHANNEL_SKIP_DAYS_ATTR,
        items = listOf(FULL_GOOGLE_ITEM, PARTIAL_GOOGLE_ITEM_1, PARTIAL_GOOGLE_ITEM_2),
        explicit = true,
        author = GOOGLE_CHANNEL_AUTHOR_ATTR,
        owner = GOOGLE_CHANNEL_OWNER_ATTR,
        block = true,
        email = GOOGLE_CHANNEL_EMAIL_ATTR
    )

    val PARTIAL_GOOGLE_CHANNEL = FULL_GOOGLE_CHANNEL.copy(
        description = null,
        image = null,
        categories = null,
        items = null,
        explicit = null,
        author = null,
        owner = null,
        block = null,
        email = null
    )

    val PARTIAL_GOOGLE_CHANNEL_2 = FULL_GOOGLE_CHANNEL.copy(
        description = null,
        image = null,
        categories = null,
        items = null,
        explicit = null,
        author = null,
        owner = null,
        block = null,
        email = null,
        textInput = CHANNEL_PARTIAL_TEXT_INPUT_ATTR,
        cloud = CHANNEL_PARTIAL_CLOULD_ATTR
    )

    // iTunes test data
    private const val ITUNES_CHANNEL_TITLE_ATTR = "channel title - iTunes"
    private const val ITUNES_CHANNEL_AUTHOR_ATTR = "channel author - iTunes"
    private const val ITUNES_CHANNEL_IMAGE_ATTR = "https://channel.image.itunes"
    private val ITUNES_CHANNEL_OWNER_ATTR = Owner(name = "channel owner name - iTunes", email = "channel.owner.email.itunes@example.com")
    private const val ITUNES_CHANNEL_CATEGORY_ATTR = "Technology - iTunes"
    private const val ITUNES_CHANNEL_LANGUAGE_ATTR = "en"
    private const val ITUNES_CHANNEL_NEW_FEED_URL_ATTR = "https://channel.new.feed.url.itunes"
    private const val ITUNES_CHANNEL_TYPE_ATTR = "channel episodic - iTunes"

    private const val ITUNES_ITEM_AUTHOR_ATTR = "item author - iTunes"
    private const val ITUNES_ITEM_TITLE_ATTR = "item title - iTunes"
    private const val ITUNES_ITEM_DURATION_ATTR = "2:00"
    private const val ITUNES_ITEM_IMAGE_ATTR = "https://item.image.itunes"
    private const val ITUNES_ITEM_EPISODE_ATTR = "full"
    private val FULL_ITUNES_ITEM = ITunesItem(
        title = ITEM_TITLE_ATTR,
        enclosure = ITEM_ENCLOSURE_ATTR,
        guid = ITEM_GUID_ATTR,
        pubDate = ITEM_PUB_DATE_ATTR,
        description = ITEM_DESCRIPTION_ATTR,
        link = ITEM_LINK_ATTR,
        author = ITUNES_ITEM_AUTHOR_ATTR,
        categories = listOf(
            ITEM_CATEGORY_ATTR,
            ITEM_CATEGORY_ATTR_WITHOUT_DOMAIN
        ),
        comments = ITEM_COMMENTS_ATTR,
        source = ITEM_SOURCE_ATTR,
        explicit = true,
        block = true,
        simpleTitle = ITUNES_ITEM_TITLE_ATTR,
        duration = ITUNES_ITEM_DURATION_ATTR,
        image = ITUNES_ITEM_IMAGE_ATTR,
        episode = 1,
        season = 1,
        episodeType = ITUNES_ITEM_EPISODE_ATTR,
    )

    private val PARTIAL_ITUNES_ITEM_1 = FULL_ITUNES_ITEM.copy(
        title = ITEM_TITLE_ATTR_PARTIAL,
        enclosure = null,
        guid = ITEM_GUID_ATTR_WITH_IS_PERMA_LINK,
        description = ITEM_DESCRIPTION_ATTR_2,
        link = null,
        author = null,
        categories = null,
        comments = null,
        source = ITEM_SOURCE_ATTR_WITHOUT_URL,
        simpleTitle = null,
        duration = null,
        image = null,
        explicit = false,
        episode = null,
        season = null,
        episodeType = null,
        block = false
    )

    private val PARTIAL_ITUNES_ITEM_2 = FULL_ITUNES_ITEM.copy(
        title = null,
        enclosure = ITEM_PARTIAL_ENCLOSURE_ATTR,
        guid = null,
        pubDate = null,
        description = null,
        link = null,
        author = null,
        categories = null,
        source = null,
        simpleTitle = null,
        duration = null,
        image = null,
        explicit = null,
        episode = null,
        season = null,
        episodeType = null,
        block = null
    )

    val FULL_ITUNES_CHANNEL = ITunesChannel(
        title = CHANNEL_TITLE_ATTR,
        description = CHANNEL_DESCRIPTION_ATTR,
        image = Image(
            link = null,
            title = null,
            url = ITUNES_CHANNEL_IMAGE_ATTR,
            description = null,
            height = null,
            width = null
        ),
        language = ITUNES_CHANNEL_LANGUAGE_ATTR,
        categories = listOf(Category(name = ITUNES_CHANNEL_CATEGORY_ATTR, domain = null)),
        link = CHANNEL_LINK_ATTR,
        copyright = CHANNEL_COPYRIGHT_ATTR,
        managingEditor = CHANNEL_MANAGING_EDITOR_ATTR,
        webMaster = CHANNEL_WEBMASTER_ATTR,
        pubDate = CHANNEL_PUB_DATE_ATTR,
        lastBuildDate = CHANNEL_LAST_BUILD_DATE_ATTR,
        generator = CHANNEL_GENERATOR_ATTR,
        docs = CHANNEL_DOCS_ATTR,
        cloud = CHANNEL_CLOUD_ATTR,
        ttl = CHANNEL_TTL_ATTR,
        rating = CHANNEL_RATING_ATTR,
        textInput = CHANNEL_TEXT_INPUT_ATTR,
        skipHours = CHANNEL_SKIP_HOURS_ATTR,
        skipDays = CHANNEL_SKIP_DAYS_ATTR,
        items = listOf(FULL_ITUNES_ITEM, PARTIAL_ITUNES_ITEM_1, PARTIAL_ITUNES_ITEM_2),
        explicit = true,
        author = ITUNES_CHANNEL_AUTHOR_ATTR,
        owner = ITUNES_CHANNEL_OWNER_ATTR,
        block = true,
        newFeedUrl = ITUNES_CHANNEL_NEW_FEED_URL_ATTR,
        simpleTitle = ITUNES_CHANNEL_TITLE_ATTR,
        type = ITUNES_CHANNEL_TYPE_ATTR,
        complete = true,
    )

    val PARTIAL_ITUNES_CHANNEL = FULL_ITUNES_CHANNEL.copy(
        image = null,
        categories = null,
        items = null,
        simpleTitle = null,
        explicit = false,
        author = null,
        owner = ITUNES_CHANNEL_OWNER_ATTR.copy(name = null),
        type = null,
        newFeedUrl = null,
        block = false,
        complete = false
    )

    val PARTIAL_ITUNES_CHANNEL_2 = FULL_ITUNES_CHANNEL.copy(
        image = null,
        categories = null,
        items = null,
        simpleTitle = null,
        explicit = null,
        author = null,
        owner = null,
        type = null,
        newFeedUrl = null,
        block = null,
        complete = null,
        cloud = CHANNEL_PARTIAL_CLOULD_ATTR,
        textInput = CHANNEL_PARTIAL_TEXT_INPUT_ATTR,
        skipDays = null,
        skipHours = null
    )
}