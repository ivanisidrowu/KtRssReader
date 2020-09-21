package tw.ktrssreader.parser

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.constant.ParserConst
import tw.ktrssreader.constant.ParserConst.CHANNEL
import tw.ktrssreader.constant.ParserConst.GOOGLE_AUTHOR
import tw.ktrssreader.constant.ParserConst.GOOGLE_BLOCK
import tw.ktrssreader.constant.ParserConst.GOOGLE_CATEGORY
import tw.ktrssreader.constant.ParserConst.GOOGLE_DESCRIPTION
import tw.ktrssreader.constant.ParserConst.GOOGLE_EMAIL
import tw.ktrssreader.constant.ParserConst.GOOGLE_EXPLICIT
import tw.ktrssreader.constant.ParserConst.GOOGLE_IMAGE
import tw.ktrssreader.constant.ParserConst.GOOGLE_OWNER
import tw.ktrssreader.constant.ParserConst.ITEM
import tw.ktrssreader.constant.ParserConst.ITUNES_AUTHOR
import tw.ktrssreader.constant.ParserConst.ITUNES_BLOCK
import tw.ktrssreader.constant.ParserConst.ITUNES_CATEGORY
import tw.ktrssreader.constant.ParserConst.ITUNES_COMPLETE
import tw.ktrssreader.constant.ParserConst.ITUNES_DURATION
import tw.ktrssreader.constant.ParserConst.ITUNES_EMAIL
import tw.ktrssreader.constant.ParserConst.ITUNES_EPISODE
import tw.ktrssreader.constant.ParserConst.ITUNES_EPISODE_TYPE
import tw.ktrssreader.constant.ParserConst.ITUNES_EXPLICIT
import tw.ktrssreader.constant.ParserConst.ITUNES_IMAGE
import tw.ktrssreader.constant.ParserConst.ITUNES_NAME
import tw.ktrssreader.constant.ParserConst.ITUNES_NEW_FEED_URL
import tw.ktrssreader.constant.ParserConst.ITUNES_OWNER
import tw.ktrssreader.constant.ParserConst.ITUNES_SEASON
import tw.ktrssreader.constant.ParserConst.ITUNES_TITLE
import tw.ktrssreader.constant.ParserConst.ITUNES_TYPE
import tw.ktrssreader.model.channel.AutoMixChannel
import tw.ktrssreader.model.channel.Image
import tw.ktrssreader.model.channel.Owner
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.model.item.AutoMixItem
import tw.ktrssreader.model.item.Category
import tw.ktrssreader.model.item.RssStandardItem
import java.io.IOException


class AutoMixParser : ParserBase<AutoMixChannel>() {
    override fun parse(xml: String) = parseAutoMixChannel(xml)

    /**
     * The priority of reading tags as the following sequence:
     * RssStandard -> iTunes -> GooglePlay
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseAutoMixChannel(xml: String): AutoMixChannel {
        val standardChannel = parseStandardChannel(xml)
        return readAutoMixChannel(xml, standardChannel)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAutoMixChannel(
        xml: String,
        standardChannel: RssStandardChannel
    ): AutoMixChannel {
        val fromITunes = parseChannel(xml) { readITunesTags(standardChannel) }
        return parseChannel(xml) { readGoogleTags(fromITunes) }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readITunesTags(standardChannel: RssStandardChannel): AutoMixChannel {
        require(XmlPullParser.START_TAG, null, CHANNEL)
        var image: Image? = standardChannel.image
        var explicit: Boolean? = null
        var categories: List<Category>? = standardChannel.categories
        var author: String? = null
        var owner: Owner? = null
        var simpleTitle: String? = null
        var type: String? = null
        var newFeedUrl: String? = null
        var block: Boolean? = null
        var complete: Boolean? = null
        val items: MutableList<AutoMixItem> = mutableListOf()
        var itemIndex = 0

        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_IMAGE -> image = readImage(standardChannel.image, ITUNES_IMAGE)
                ITUNES_EXPLICIT -> explicit = readString(ITUNES_EXPLICIT)?.toBoolean()
                ITUNES_CATEGORY -> categories.doActionOrSkip(this) { categories = readCategory(ITUNES_CATEGORY) }
                ITUNES_AUTHOR -> author = readString(ITUNES_AUTHOR)
                ITUNES_OWNER -> owner = readITunesOwner()
                ITUNES_TITLE -> simpleTitle = readString(ITUNES_TITLE)
                ITUNES_TYPE -> type = readString(ITUNES_TYPE)
                ITUNES_NEW_FEED_URL -> newFeedUrl = readString(ITUNES_NEW_FEED_URL)
                ITUNES_BLOCK -> block = readString(ITUNES_BLOCK)?.convertYesNo()
                ITUNES_COMPLETE -> complete = readString(ITUNES_COMPLETE)?.convertYesNo()
                ITEM -> {
                    standardChannel.items?.get(itemIndex)?.let {
                        items.add(readITunesItem(it))
                        itemIndex++
                    }
                }
                else -> skip()
            }
        }

        require(XmlPullParser.END_TAG, null, CHANNEL)
        return AutoMixChannel(
            title = standardChannel.title,
            description = standardChannel.description,
            image = image,
            language = standardChannel.language,
            categories = categories,
            link = standardChannel.link,
            copyright = standardChannel.copyright,
            managingEditor = standardChannel.managingEditor,
            webMaster = standardChannel.webMaster,
            pubDate = standardChannel.pubDate,
            lastBuildDate = standardChannel.lastBuildDate,
            generator = standardChannel.generator,
            docs = standardChannel.docs,
            cloud = standardChannel.cloud,
            ttl = standardChannel.ttl,
            rating = standardChannel.rating,
            textInput = standardChannel.textInput,
            skipHours = standardChannel.skipHours,
            skipDays = standardChannel.skipDays,
            items = items.takeIf { it.isNotEmpty() },
            simpleTitle = simpleTitle,
            explicit = explicit,
            email = null,
            author = author,
            owner = owner,
            type = type,
            newFeedUrl = newFeedUrl,
            block = block,
            complete = complete
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readImage(standardImage: Image?, tagName: String): Image {
        require(XmlPullParser.START_TAG, null, tagName)
        val href: String? = getAttributeValue(null, ParserConst.HREF)
        if (next() == XmlPullParser.TEXT) {
            nextTag()
        }
        require(XmlPullParser.END_TAG, null, tagName)
        return Image(
            link = standardImage?.link,
            title = standardImage?.title,
            url = standardImage?.url ?: href,
            description = standardImage?.description,
            height = standardImage?.height,
            width = standardImage?.width
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readCategory(tagName: String): List<Category>? {
        require(XmlPullParser.START_TAG, null, tagName)
        val categories = mutableListOf<Category>()
        getAttributeValue(null, ParserConst.TEXT)?.let { categories.add(Category(name = it, null)) }
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                tagName -> {
                    getAttributeValue(null, ParserConst.TEXT)
                        ?.let { categories.add(Category(name = it, domain = null)) }
                    nextTag()
                }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, tagName)
        return categories
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readITunesOwner(): Owner {
        require(XmlPullParser.START_TAG, null, ITUNES_OWNER)
        var name: String? = null
        var email: String? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (this.name) {
                ITUNES_NAME -> name = readString(ITUNES_NAME)
                ITUNES_EMAIL -> email = readString(ITUNES_EMAIL)
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITUNES_OWNER)
        return Owner(name = name, email = email)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readITunesItem(standardItem: RssStandardItem): AutoMixItem {
        require(XmlPullParser.START_TAG, null, ITEM)
        var simpleTitle: String? = null
        var duration: String? = null
        var image: String? = null
        var explicit: Boolean? = null
        var episode: Int? = null
        var season: Int? = null
        var episodeType: String? = null
        var block: Boolean? = null
        var author: String? = standardItem.author
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_DURATION -> duration = readString(ITUNES_DURATION)
                ITUNES_IMAGE -> image = readImage(null, ITUNES_IMAGE).url
                ITUNES_EXPLICIT -> explicit = readString(ITUNES_EXPLICIT)?.toBoolean()
                ITUNES_TITLE -> simpleTitle = readString(ITUNES_TITLE)
                ITUNES_EPISODE -> episode = readString(ITUNES_EPISODE)?.toIntOrNull()
                ITUNES_SEASON -> season = readString(ITUNES_SEASON)?.toIntOrNull()
                ITUNES_EPISODE_TYPE -> episodeType = readString(ITUNES_EPISODE_TYPE)
                ITUNES_BLOCK -> block = readString(ITUNES_BLOCK)?.convertYesNo()
                ITUNES_AUTHOR -> author = readString(ITUNES_AUTHOR)
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITEM)
        return AutoMixItem(
            title = standardItem.title,
            enclosure = standardItem.enclosure,
            guid = standardItem.guid,
            pubDate = standardItem.pubDate,
            description = standardItem.description,
            link = standardItem.link,
            author = author,
            categories = standardItem.categories,
            comments = standardItem.comments,
            source = standardItem.source,
            simpleTitle = simpleTitle,
            duration = duration,
            image = image,
            explicit = explicit,
            episode = episode,
            season = season,
            episodeType = episodeType,
            block = block
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readGoogleTags(previousResult: AutoMixChannel): AutoMixChannel {
        require(XmlPullParser.START_TAG, null, CHANNEL)
        var description: String? = previousResult.description
        var image: Image? = previousResult.image
        var explicit: Boolean? = previousResult.explicit
        var categories: List<Category>? = previousResult.categories
        var author: String? = previousResult.author
        var owner: Owner? = previousResult.owner
        var block: Boolean? = previousResult.block
        var email: String? = previousResult.email
        val items: MutableList<AutoMixItem> = mutableListOf()
        var itemIndex = 0
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                GOOGLE_DESCRIPTION -> description.doActionOrSkip(this) { description = readString(GOOGLE_DESCRIPTION) }
                GOOGLE_IMAGE -> image = readImage(previousResult.image, GOOGLE_IMAGE)
                GOOGLE_EXPLICIT -> explicit.doActionOrSkip(this) { explicit = readString(GOOGLE_EXPLICIT)?.convertYesNo() }
                GOOGLE_CATEGORY -> categories.doActionOrSkip(this) { categories = readCategory(GOOGLE_CATEGORY) }
                GOOGLE_AUTHOR -> author.doActionOrSkip(this) { author = readString(GOOGLE_AUTHOR) }
                GOOGLE_OWNER -> owner = readGoogleOwner(previousResult.owner)
                GOOGLE_BLOCK -> block.doActionOrSkip(this) { block = readString(GOOGLE_BLOCK)?.convertYesNo() }
                GOOGLE_EMAIL -> email.doActionOrSkip(this) { email = readString(GOOGLE_EMAIL) }
                ITEM -> {
                    previousResult.items?.get(itemIndex)?.let {
                        items.add(readGoogleItem(it))
                        itemIndex++
                    }
                }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, CHANNEL)
        return AutoMixChannel(
            title = previousResult.title,
            description = description,
            image = image,
            language = previousResult.language,
            categories = categories?.takeIf { it.isNotEmpty() },
            link = previousResult.link,
            copyright = previousResult.copyright,
            managingEditor = previousResult.managingEditor,
            webMaster = previousResult.webMaster,
            pubDate = previousResult.pubDate,
            lastBuildDate = previousResult.lastBuildDate,
            generator = previousResult.generator,
            docs = previousResult.docs,
            cloud = previousResult.cloud,
            ttl = previousResult.ttl,
            rating = previousResult.rating,
            textInput = previousResult.textInput,
            skipHours = previousResult.skipHours,
            skipDays = previousResult.skipDays,
            items = items.takeIf { it.isNotEmpty() },
            simpleTitle = previousResult.simpleTitle,
            explicit = explicit,
            email = email,
            author = author,
            owner = owner,
            type = previousResult.type,
            newFeedUrl = previousResult.newFeedUrl,
            block = block,
            complete = previousResult.complete
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readGoogleOwner(previousResult: Owner?): Owner? {
        return if (previousResult?.email?.isEmpty() == false) {
            previousResult
        } else {
            Owner(name = previousResult?.name, email = readString(GOOGLE_OWNER))
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readGoogleItem(previousResult: AutoMixItem): AutoMixItem {
        require(XmlPullParser.START_TAG, null, ITEM)
        var description: String? = previousResult.description
        var explicit: Boolean? = previousResult.explicit
        var block: Boolean? = previousResult.block
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                GOOGLE_DESCRIPTION -> description.doActionOrSkip(this) { description = readString(GOOGLE_DESCRIPTION) }
                GOOGLE_EXPLICIT -> explicit.doActionOrSkip(this) { explicit = readString(GOOGLE_EXPLICIT)?.convertYesNo() }
                GOOGLE_BLOCK -> block.doActionOrSkip(this) { block = readString(GOOGLE_BLOCK)?.convertYesNo() }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITEM)
        return AutoMixItem(
            title = previousResult.title,
            enclosure = previousResult.enclosure,
            guid = previousResult.guid,
            pubDate = previousResult.pubDate,
            description = description,
            link = previousResult.link,
            author = previousResult.author,
            categories = previousResult.categories,
            comments = previousResult.comments,
            source = previousResult.source,
            simpleTitle = previousResult.simpleTitle,
            duration = previousResult.duration,
            image = previousResult.image,
            explicit = explicit,
            episode = previousResult.episode,
            season = previousResult.season,
            episodeType = previousResult.episodeType,
            block = block
        )
    }

    private inline fun Any?.doActionOrSkip(parser: XmlPullParser, action: () -> Unit) {
        return if (this == null) {
            action()
        } else {
            parser.skip()
        }
    }
}