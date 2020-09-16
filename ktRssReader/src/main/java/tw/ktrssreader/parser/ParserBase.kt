package tw.ktrssreader.parser

import android.nfc.tech.NfcA
import android.util.Xml
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.model.channel.Cloud
import tw.ktrssreader.model.channel.Image
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.model.channel.TextInput
import tw.ktrssreader.model.item.RssStandardItem
import tw.ktrssreader.parser.ParserConst.CATEGORY
import tw.ktrssreader.parser.ParserConst.CLOUD
import tw.ktrssreader.parser.ParserConst.COPYRIGHT
import tw.ktrssreader.parser.ParserConst.DESCRIPTION
import tw.ktrssreader.parser.ParserConst.DOCS
import tw.ktrssreader.parser.ParserConst.DOMAIN
import tw.ktrssreader.parser.ParserConst.GENERATOR
import tw.ktrssreader.parser.ParserConst.IMAGE
import tw.ktrssreader.parser.ParserConst.ITEM
import tw.ktrssreader.parser.ParserConst.LANGUAGE
import tw.ktrssreader.parser.ParserConst.LAST_BUILD_DATE
import tw.ktrssreader.parser.ParserConst.LINK
import tw.ktrssreader.parser.ParserConst.MANAGING_EDITOR
import tw.ktrssreader.parser.ParserConst.NAME
import tw.ktrssreader.parser.ParserConst.PATH
import tw.ktrssreader.parser.ParserConst.PORT
import tw.ktrssreader.parser.ParserConst.PROTOCOL
import tw.ktrssreader.parser.ParserConst.PUB_DATE
import tw.ktrssreader.parser.ParserConst.RATING
import tw.ktrssreader.parser.ParserConst.REGISTER_PROCEDURE
import tw.ktrssreader.parser.ParserConst.SKIP_DAYS
import tw.ktrssreader.parser.ParserConst.SKIP_HOURS
import tw.ktrssreader.parser.ParserConst.TEXT_INPUT
import tw.ktrssreader.parser.ParserConst.TITLE
import tw.ktrssreader.parser.ParserConst.TTL
import tw.ktrssreader.parser.ParserConst.URL
import tw.ktrssreader.parser.ParserConst.WEB_MASTER
import java.io.ByteArrayInputStream
import java.io.IOException

open class ParserBase : Parser<RssStandardChannel> {

    @Throws(XmlPullParserException::class)
    override fun parse(xml: String): RssStandardChannel {
        val parser = getXmlParser(xml)

        var result: RssStandardChannel? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue

            if (parser.name == ParserConst.CHANNEL) {
                result = readChannel(parser)
                break
            } else {
                parser.skip()
            }
        }
        return result ?: throw XmlPullParserException("No valid channel tag in the RSS feed.")
    }

    override suspend fun parseSuspend(xml: String) = parse(xml)

    override fun parseFlow(xml: String): Flow<RssStandardChannel> {
        return flow { emit(parse(xml)) }
    }

    protected fun getXmlParser(xml: String): XmlPullParser {
        // TODO: temp test
        val stream = javaClass.getResourceAsStream("/test_data.xml")!!
        val content = stream.bufferedReader().use { it.readText() }

        ByteArrayInputStream(content.toByteArray()).use { inputStream ->
            return Xml.newPullParser().apply {
                setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                setInput(inputStream, null)
                nextTag()
            }
        }
    }

    open fun readChannel(parser: XmlPullParser): RssStandardChannel {
        parser.require(XmlPullParser.START_TAG, null, ParserConst.CHANNEL)
        var title: String? = null
        var description: String? = null
        var image: Image? = null
        var language: String? = null
        val categories = arrayListOf<String>()
        var link: String? = null
        var copyright: String? = null
        var managingEditor: String? = null
        var webMaster: String? = null
        var pubDate: String? = null
        var lastBuildDate: String? = null
        var generator: String? = null
        var docs: String? = null
        var cloud: Cloud? = null
        var ttl: Int? = null
        var rating: Float? = null
        var textInput: TextInput? = null
        var skipHours: Int? = null
        var skipDays: String? = null
        var items: List<RssStandardItem> = listOf()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TITLE -> title = parser.readString(TITLE)
                DESCRIPTION -> description = parser.readString(DESCRIPTION)
                LINK -> link = parser.readString(LINK)
                IMAGE -> image = readImage(parser)
                LANGUAGE -> language = parser.readString(IMAGE)
                CATEGORY -> parser.readString(CATEGORY)?.let { categories.add(it) }
                COPYRIGHT -> copyright = parser.readString(COPYRIGHT)
                MANAGING_EDITOR -> managingEditor = parser.readString(MANAGING_EDITOR)
                WEB_MASTER -> webMaster = parser.readString(WEB_MASTER)
                PUB_DATE -> pubDate = parser.readString(PUB_DATE)
                LAST_BUILD_DATE -> lastBuildDate = parser.readString(LAST_BUILD_DATE)
                GENERATOR -> generator = parser.readString(GENERATOR)
                DOCS -> docs = parser.readString(DOCS)
                CLOUD -> cloud = parser.readCloud()
                TTL -> ttl = parser.readString(TTL)?.toIntOrNull()
                RATING -> rating = parser.readString(RATING)?.toFloatOrNull()
                TEXT_INPUT -> textInput = parser.readTextInput()
                SKIP_HOURS -> skipHours = parser.readString(SKIP_HOURS)?.toIntOrNull()
                SKIP_DAYS -> skipDays = parser.readString(SKIP_DAYS)
                ITEM -> items = readItems()
                else -> parser.skip()
            }
        }

        return RssStandardChannel(
            title = title,
            description = description,
            image = image,
            language = language,
            categories = listOf(),
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

    open fun readItems(): List<RssStandardItem> {
        return listOf()
    }

    open fun readImage(parser: XmlPullParser): Image? {
        parser.require(XmlPullParser.START_TAG, null, IMAGE)
        var link: String? = null
        var title: String? = null
        var url: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue

            when (parser.name) {
                LINK -> link = parser.readString(LINK)
                TITLE -> title = parser.readString(TITLE)
                URL -> url = parser.readString(URL)
                else -> parser.skip()
            }
        }
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, IMAGE)
        return Image(link = link, title = title, url = url)
    }

abstract class ParserBase<out T : RssStandardChannel> : Parser<T>
    protected fun XmlPullParser.readCloud(): Cloud {
        require(XmlPullParser.START_TAG, null, CLOUD)
        var domain: String? = null
        var port: Int? = null
        var path: String? = null
        var registerProcedure: String? = null
        var protocol: String? = null
        val map = readAttributes(CLOUD, DOMAIN, PORT, PATH, REGISTER_PROCEDURE, PROTOCOL)
        map.forEach { entry ->
            when (entry.key) {
                DOMAIN -> domain = entry.value
                PORT -> port = entry.value.toIntOrNull()
                PATH -> path = entry.value
                REGISTER_PROCEDURE -> registerProcedure = entry.value
                PROTOCOL -> protocol = entry.value
            }
        }
        nextTag()
        require(XmlPullParser.END_TAG, null, CLOUD)
        return Cloud(
            domain = domain,
            port = port,
            path = path,
            registerProcedure = registerProcedure,
            protocol = protocol
        )
    }

    protected fun XmlPullParser.readTextInput(): TextInput {
        require(XmlPullParser.START_TAG, null, TEXT_INPUT)
        var title: String? = null
        var description: String? = null
        var name: String? = null
        var link: String? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                TITLE -> title = readString(TITLE)
                DESCRIPTION -> description = readString(DESCRIPTION)
                NAME -> name = readString(NAME)
                LINK -> link = readString(LINK)
            }
        }
        nextTag()
        require(XmlPullParser.END_TAG, null, TEXT_INPUT)
        return TextInput(title = title, description = description, name = name, link = link)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    protected fun XmlPullParser.readString(tagName: String): String? {
        require(XmlPullParser.START_TAG, null, tagName)
        var content: String? = null
        if (next() == XmlPullParser.TEXT) {
            content = text
            nextTag()
        }
        require(XmlPullParser.END_TAG, null, tagName)
        return content
    }

    @Throws(IOException::class, XmlPullParserException::class)
    protected fun XmlPullParser.readAttributes(
        tagName: String,
        vararg attrs: String
    ): Map<String, String> {
        require(XmlPullParser.START_TAG, null, tagName)

        val map = mutableMapOf<String, String>()
        attrs.forEach { attr ->
            map[attr] = getAttributeValue(null, attr)
        }

        require(XmlPullParser.END_TAG, null, tagName)
        return map
    }

    @Throws(XmlPullParserException::class, IOException::class)
    protected fun XmlPullParser.skip() {
        if (eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}