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
import tw.ktrssreader.model.channel.GoogleChannelData
import tw.ktrssreader.model.channel.Image
import tw.ktrssreader.model.channel.Owner
import tw.ktrssreader.model.channel.RssStandardChannelData
import tw.ktrssreader.model.item.Category
import tw.ktrssreader.model.item.GoogleItemData
import tw.ktrssreader.model.item.RssStandardItem
import java.io.IOException

class GoogleParser : ParserBase<GoogleChannelData>() {

    override fun parse(xml: String) = parserGoogleChannel(xml)

    private fun parserGoogleChannel(xml: String): GoogleChannelData {
        val standardChannel = parseStandardChannel(xml)
        return parseChannel(xml) { readGoogleChannel(standardChannel) }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readGoogleChannel(standardChannel: RssStandardChannelData): GoogleChannelData {
        require(XmlPullParser.START_TAG, null, CHANNEL)
        var description: String? = null
        var image: Image? = null
        var explicit: Boolean? = null
        var categories: List<Category>? = null
        var author: String? = null
        var owner: Owner? = null
        var block: Boolean? = null
        var email: String? = null
        val items: MutableList<GoogleItemData> = mutableListOf()
        var itemIndex = 0
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                GOOGLE_DESCRIPTION -> description = readString(GOOGLE_DESCRIPTION)
                GOOGLE_IMAGE -> image = readImage()
                GOOGLE_EXPLICIT -> explicit = readString(GOOGLE_EXPLICIT)?.convertYesNo()
                GOOGLE_CATEGORY -> categories = readCategory()
                GOOGLE_AUTHOR -> author = readString(GOOGLE_AUTHOR)
                GOOGLE_OWNER -> owner = Owner(name = null, email = readString(GOOGLE_OWNER))
                GOOGLE_BLOCK -> block = readString(GOOGLE_BLOCK)?.convertYesNo()
                GOOGLE_EMAIL -> email = readString(GOOGLE_EMAIL)
                ITEM -> {
                    standardChannel.items?.get(itemIndex)?.let {
                        items.add(readItem(it))
                        itemIndex++
                    }
                }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, CHANNEL)
        return GoogleChannelData(
            title = standardChannel.title,
            description = description,
            image = image,
            language = standardChannel.language,
            categories = categories?.takeIf { it.isNotEmpty() },
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
            explicit = explicit,
            author = author,
            owner = owner,
            block = block,
            email = email
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readItem(standardItem: RssStandardItem): GoogleItemData {
        require(XmlPullParser.START_TAG, null, ITEM)
        var description: String? = null
        var explicit: Boolean? = null
        var block: Boolean? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                GOOGLE_DESCRIPTION -> description = readString(GOOGLE_DESCRIPTION)
                GOOGLE_EXPLICIT -> explicit = readString(GOOGLE_EXPLICIT)?.convertYesNo()
                GOOGLE_BLOCK -> block = readString(GOOGLE_BLOCK)?.convertYesNo()
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITEM)
        return GoogleItemData(
            title = standardItem.title,
            enclosure = standardItem.enclosure,
            guid = standardItem.guid,
            pubDate = standardItem.pubDate,
            description = description,
            link = standardItem.link,
            author = standardItem.author,
            categories = standardItem.categories,
            comments = standardItem.comments,
            source = standardItem.source,
            explicit = explicit,
            block = block
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readImage(): Image {
        require(XmlPullParser.START_TAG, null, GOOGLE_IMAGE)
        val href: String? = getAttributeValue(null, ParserConst.HREF)
        nextTag()
        require(XmlPullParser.END_TAG, null, GOOGLE_IMAGE)
        return Image(
            link = null,
            title = null,
            url = href,
            description = null,
            height = null,
            width = null
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readCategory(): List<Category> {
        require(XmlPullParser.START_TAG, null, GOOGLE_CATEGORY)
        val categories = mutableListOf<Category>()
        getAttributeValue(null, ParserConst.TEXT)?.let {
            categories.add(Category(name = it, domain = null))
        }
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                GOOGLE_CATEGORY -> {
                    getAttributeValue(null, ParserConst.TEXT)
                        ?.let { categories.add(Category(name = it, domain = null)) }
                    nextTag()
                }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, GOOGLE_CATEGORY)
        return categories
    }
}