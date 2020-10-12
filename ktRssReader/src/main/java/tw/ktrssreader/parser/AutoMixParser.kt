/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tw.ktrssreader.parser

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.constant.ParserConst
import tw.ktrssreader.constant.ParserConst.AUTHOR
import tw.ktrssreader.constant.ParserConst.BLOCK
import tw.ktrssreader.constant.ParserConst.CATEGORY
import tw.ktrssreader.constant.ParserConst.CLOUD
import tw.ktrssreader.constant.ParserConst.COMMENTS
import tw.ktrssreader.constant.ParserConst.COPYRIGHT
import tw.ktrssreader.constant.ParserConst.DESCRIPTION
import tw.ktrssreader.constant.ParserConst.DOCS
import tw.ktrssreader.constant.ParserConst.DOMAIN
import tw.ktrssreader.constant.ParserConst.EMAIL
import tw.ktrssreader.constant.ParserConst.ENCLOSURE
import tw.ktrssreader.constant.ParserConst.EXPLICIT
import tw.ktrssreader.constant.ParserConst.GENERATOR
import tw.ktrssreader.constant.ParserConst.GOOGLE_AUTHOR
import tw.ktrssreader.constant.ParserConst.GOOGLE_BLOCK
import tw.ktrssreader.constant.ParserConst.GOOGLE_CATEGORY
import tw.ktrssreader.constant.ParserConst.GOOGLE_DESCRIPTION
import tw.ktrssreader.constant.ParserConst.GOOGLE_EMAIL
import tw.ktrssreader.constant.ParserConst.GOOGLE_EXPLICIT
import tw.ktrssreader.constant.ParserConst.GOOGLE_IMAGE
import tw.ktrssreader.constant.ParserConst.GOOGLE_OWNER
import tw.ktrssreader.constant.ParserConst.GUID
import tw.ktrssreader.constant.ParserConst.HEIGHT
import tw.ktrssreader.constant.ParserConst.HREF
import tw.ktrssreader.constant.ParserConst.IMAGE
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
import tw.ktrssreader.constant.ParserConst.LANGUAGE
import tw.ktrssreader.constant.ParserConst.LAST_BUILD_DATE
import tw.ktrssreader.constant.ParserConst.LINK
import tw.ktrssreader.constant.ParserConst.MANAGING_EDITOR
import tw.ktrssreader.constant.ParserConst.OWNER
import tw.ktrssreader.constant.ParserConst.PUB_DATE
import tw.ktrssreader.constant.ParserConst.RATING
import tw.ktrssreader.constant.ParserConst.SKIP_DAYS
import tw.ktrssreader.constant.ParserConst.SKIP_HOURS
import tw.ktrssreader.constant.ParserConst.SOURCE
import tw.ktrssreader.constant.ParserConst.TEXT
import tw.ktrssreader.constant.ParserConst.TEXT_INPUT
import tw.ktrssreader.constant.ParserConst.TITLE
import tw.ktrssreader.constant.ParserConst.TTL
import tw.ktrssreader.constant.ParserConst.TYPE
import tw.ktrssreader.constant.ParserConst.URL
import tw.ktrssreader.constant.ParserConst.WEB_MASTER
import tw.ktrssreader.constant.ParserConst.WIDTH
import tw.ktrssreader.model.channel.*
import tw.ktrssreader.model.item.*
import tw.ktrssreader.utils.logD
import java.io.IOException

class AutoMixParser : ParserBase<AutoMixChannelData>() {

    private val rssStandardMap = HashMap<String, Any?>(19)
    private val iTunesMap = HashMap<String, Any?>(11)
    private val googlePlayMap = HashMap<String, Any?>(8)

    override val logTag: String = this::class.java.simpleName

    override fun parse(xml: String): AutoMixChannelData {
        parseTag(xml)

        return AutoMixChannelData(
            title = priorityMapValueOf(TITLE),
            description = priorityMapValueOf(DESCRIPTION),
            image = priorityMapValueOf(IMAGE),
            language = priorityMapValueOf(LANGUAGE),
            categories = priorityMapValueOf(CATEGORY),
            link = priorityMapValueOf(LINK),
            copyright = priorityMapValueOf(COPYRIGHT),
            managingEditor = priorityMapValueOf(MANAGING_EDITOR),
            webMaster = priorityMapValueOf(WEB_MASTER),
            pubDate = priorityMapValueOf(PUB_DATE),
            lastBuildDate = priorityMapValueOf(LAST_BUILD_DATE),
            generator = priorityMapValueOf(GENERATOR),
            docs = priorityMapValueOf(DOCS),
            cloud = priorityMapValueOf(CLOUD),
            ttl = priorityMapValueOf(TTL),
            rating = priorityMapValueOf(RATING),
            textInput = priorityMapValueOf(TEXT_INPUT),
            skipHours = priorityMapValueOf(SKIP_HOURS),
            skipDays = priorityMapValueOf(SKIP_DAYS),
            items = priorityMapValueOf(ITEM),
            simpleTitle = priorityMapValueOf(ITUNES_TITLE),
            explicit = priorityMapValueOf(EXPLICIT),
            email = priorityMapValueOf(EMAIL),
            author = priorityMapValueOf(AUTHOR),
            owner = priorityMapValueOf(OWNER),
            type = priorityMapValueOf(TYPE),
            newFeedUrl = priorityMapValueOf(ITUNES_NEW_FEED_URL),
            block = priorityMapValueOf(BLOCK),
            complete = priorityMapValueOf(ITUNES_COMPLETE),
        )
    }

    /**
     * The priority of reading tags as the following sequence:
     * RssStandard -> iTunes -> GooglePlay
     */
    private fun <R> priorityMapValueOf(key: String): R? {
        @Suppress("UNCHECKED_CAST")
        return (rssStandardMap[key] ?: iTunesMap[key] ?: googlePlayMap[key]) as? R
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseTag(xml: String) = parseChannel<Unit>(xml = xml) {
        require(XmlPullParser.START_TAG, null, ParserConst.CHANNEL)

        var title: String? = null
        var description: String? = null
        var image: Image? = null
        var language: String? = null
        val categories = mutableListOf<Category>()
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
        var rating: String? = null
        var textInput: TextInput? = null
        var skipHours: List<Int>? = null
        var skipDays: List<String>? = null
        val items = mutableListOf<AutoMixItemData>()

        var iTunesImageHref: String? = null
        val iTunesCategories = mutableListOf<Category>()
        var iTunesSimpleTitle: String? = null
        var iTunesExplicit: Boolean? = null
        var iTunesEmail: String? = null
        var iTunesAuthor: String? = null
        var iTunesOwner: Owner? = null
        var iTunesType: String? = null
        var iTunesNewFeedUrl: String? = null
        var iTunesBlock: Boolean? = null
        var iTunesComplete: Boolean? = null

        var googleDescription: String? = null
        var googleImageHref: String? = null
        val googleCategories = mutableListOf<Category?>()
        var googleExplicit: Boolean? = null
        var googleEmail: String? = null
        var googleAuthor: String? = null
        var googleOwner: Owner? = null
        var googleBlock: Boolean? = null

        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                TITLE -> title = readString(TITLE)
                DESCRIPTION -> description = readString(DESCRIPTION)
                LINK -> link = readString(LINK)
                IMAGE -> image = readImage()
                LANGUAGE -> language = readString(LANGUAGE)
                CATEGORY -> readCategory(CATEGORY)?.let { categories.add(it) }
                COPYRIGHT -> copyright = readString(COPYRIGHT)
                MANAGING_EDITOR -> managingEditor = readString(MANAGING_EDITOR)
                WEB_MASTER -> webMaster = readString(WEB_MASTER)
                PUB_DATE -> pubDate = readString(PUB_DATE)
                LAST_BUILD_DATE -> lastBuildDate = readString(LAST_BUILD_DATE)
                GENERATOR -> generator = readString(GENERATOR)
                DOCS -> docs = readString(DOCS)
                CLOUD -> cloud = readCloud()
                TTL -> ttl = readString(TTL)?.toIntOrNull()
                RATING -> rating = readString(RATING)
                TEXT_INPUT -> textInput = readTextInput()
                SKIP_HOURS -> skipHours = readSkipHours()
                SKIP_DAYS -> skipDays = readSkipDays()
                ITEM -> items.add(readItem())

                ITUNES_IMAGE -> iTunesImageHref = readImageHref(ITUNES_IMAGE)
                ITUNES_CATEGORY -> readCategory(ITUNES_CATEGORY)?.let { iTunesCategories.add(it) }
                ITUNES_TITLE -> iTunesSimpleTitle = readString(ITUNES_TITLE)
                ITUNES_EXPLICIT -> iTunesExplicit = readString(ITUNES_EXPLICIT)?.convertYesNo()
                ITUNES_EMAIL -> iTunesEmail = readString(ITUNES_EMAIL)
                ITUNES_AUTHOR -> iTunesAuthor = readString(ITUNES_AUTHOR)
                ITUNES_OWNER -> iTunesOwner = readITunesOwner()
                ITUNES_TYPE -> iTunesType = readString(ITUNES_TYPE)
                ITUNES_NEW_FEED_URL -> iTunesNewFeedUrl = readString(ITUNES_NEW_FEED_URL)
                ITUNES_BLOCK -> iTunesBlock = readString(ITUNES_BLOCK)?.convertYesNo()
                ITUNES_COMPLETE -> iTunesComplete = readString(ITUNES_COMPLETE)?.convertYesNo()

                GOOGLE_DESCRIPTION -> googleDescription = readString(GOOGLE_DESCRIPTION)
                GOOGLE_IMAGE -> googleImageHref = readImageHref(GOOGLE_IMAGE)
                GOOGLE_CATEGORY -> googleCategories.add(readCategory(GOOGLE_CATEGORY))
                GOOGLE_EXPLICIT -> googleExplicit = readString(GOOGLE_EXPLICIT)?.convertYesNo()
                GOOGLE_EMAIL -> googleEmail = readString(GOOGLE_EMAIL)
                GOOGLE_AUTHOR -> googleAuthor = readString(GOOGLE_AUTHOR)
                GOOGLE_OWNER -> googleOwner = readGoogleOwner()
                GOOGLE_BLOCK -> googleBlock = readString(GOOGLE_BLOCK)?.convertYesNo()

                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ParserConst.CHANNEL)

        rssStandardMap.run {
            put(TITLE, title)
            put(DESCRIPTION, description)
            put(IMAGE, image.replaceInvalidUrlByPriority(iTunesImageHref, googleImageHref))
            put(LANGUAGE, language)
            put(CATEGORY, categories.takeIf { it.isNotEmpty() })
            put(LINK, link)
            put(COPYRIGHT, copyright)
            put(MANAGING_EDITOR, managingEditor)
            put(WEB_MASTER, webMaster)
            put(PUB_DATE, pubDate)
            put(LAST_BUILD_DATE, lastBuildDate)
            put(GENERATOR, generator)
            put(DOCS, docs)
            put(CLOUD, cloud)
            put(TTL, ttl)
            put(RATING, rating)
            put(TEXT_INPUT, textInput)
            put(SKIP_HOURS, skipHours)
            put(SKIP_DAYS, skipDays)
            put(ITEM, items.takeIf { it.isNotEmpty() })
        }
        iTunesMap.run {
            put(IMAGE, hrefToImage(iTunesImageHref))
            put(CATEGORY, iTunesCategories.takeIf { it.isNotEmpty() })
            put(ITUNES_TITLE, iTunesSimpleTitle)
            put(EXPLICIT, iTunesExplicit)
            put(EMAIL, iTunesEmail)
            put(AUTHOR, iTunesAuthor)
            put(OWNER, iTunesOwner)
            put(TYPE, iTunesType)
            put(ITUNES_NEW_FEED_URL, iTunesNewFeedUrl)
            put(BLOCK, iTunesBlock)
            put(ITUNES_COMPLETE, iTunesComplete)
        }
        googlePlayMap.run {
            put(DESCRIPTION, googleDescription)
            put(IMAGE, hrefToImage(googleImageHref))
            put(CATEGORY, googleCategories.takeIf { it.isNotEmpty() })
            put(EXPLICIT, googleExplicit)
            put(EMAIL, googleEmail)
            put(AUTHOR, googleAuthor)
            put(OWNER, googleOwner)
            put(BLOCK, googleBlock)
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readImage(): Image? {
        require(XmlPullParser.START_TAG, null, IMAGE)
        var link: String? = null
        var title: String? = null
        var url: String? = null
        var description: String? = null
        var height: Int? = null
        var width: Int? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            logD(logTag, "[readImage]: RSS 2.0 tag name = $name.")
            when (name) {
                LINK -> link = readString(LINK)
                TITLE -> title = readString(TITLE)
                URL -> url = readString(URL)
                DESCRIPTION -> description = readString(DESCRIPTION)
                HEIGHT -> height = readString(HEIGHT)?.toIntOrNull()
                WIDTH -> width = readString(WIDTH)?.toIntOrNull()
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, IMAGE)
        return Image(
            link = link,
            title = title,
            url = url,
            description = description,
            height = height,
            width = width
        ).takeIf {
            it.link != null || it.title != null || it.url != null
                    || it.description != null || it.height != null || it.width != null
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readImageHref(tagName: String): String? {
        var href: String? = null
        readAttributes(tagName, listOf(HREF)) { attr, value ->
            if (attr == HREF) href = value
        }
        return href
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readCategory(tagName: String): Category? {
        when (tagName) {
            ITUNES_CATEGORY, GOOGLE_CATEGORY -> {
                var text: String? = null
                readAttributes(tagName, listOf(TEXT)) { attr, value ->
                    if (attr == TEXT) text = value
                }
                text ?: return null
                return Category(name = text, domain = null)
            }
            CATEGORY -> {
                require(XmlPullParser.START_TAG, null, CATEGORY)
                val domain: String? = getAttributeValue(null, DOMAIN)
                val name: String? = readString(tagName = CATEGORY)
                require(XmlPullParser.END_TAG, null, CATEGORY)
                logD(logTag, "[readCategory]: name = $name, domain = $domain")
                return if (name == null && domain == null) {
                    null
                } else {
                    Category(name = name, domain = domain)
                }
            }
            else -> return null
        }
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
        logD(logTag, "[readOwner] name = $name, email = $email")
        return Owner(name = name, email = email)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readGoogleOwner(): Owner? {
        val email = readString(GOOGLE_OWNER) ?: return null
        val owner = Owner(name = null, email = email)
        logD(logTag, "[readGoogleOwner]: $owner")
        return owner
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readItem(): AutoMixItemData {
        require(XmlPullParser.START_TAG, null, ITEM)
        var title: String? = null
        var enclosure: Enclosure? = null
        var guid: Guid? = null
        var pubDate: String? = null
        var description: String? = null
        var link: String? = null
        var author: String? = null
        val categories = mutableListOf<Category>()
        var comments: String? = null
        var source: Source? = null

        var iTunesAuthor: String? = null
        var iTunesSimpleTitle: String? = null
        var iTunesDuration: String? = null
        var iTunesImageHref: String? = null
        var iTunesExplicit: Boolean? = null
        var iTunesEpisode: Int? = null
        var iTunesSeason: Int? = null
        var iTunesEpisodeType: String? = null
        var iTunesBlock: Boolean? = null

        var googleDescription: String? = null
        var googleExplicit: Boolean? = null
        var googleBlock: Boolean? = null

        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            logD(logTag, "[readRssStandardItem] Reading tag name $name.")
            when (this.name) {
                TITLE -> title = readString(TITLE)
                ENCLOSURE -> enclosure = readEnclosure()
                GUID -> guid = readGuid()
                PUB_DATE -> pubDate = readString(PUB_DATE)
                DESCRIPTION -> description = readString(DESCRIPTION)
                LINK -> link = readString(LINK)
                AUTHOR -> author = readString(AUTHOR)
                CATEGORY -> readCategory(CATEGORY)?.let { categories.add(it) }
                COMMENTS -> comments = readString(COMMENTS)
                SOURCE -> source = readSource()

                ITUNES_AUTHOR -> iTunesAuthor = readString(ITUNES_AUTHOR)
                ITUNES_TITLE -> iTunesSimpleTitle = readString(ITUNES_TITLE)
                ITUNES_DURATION -> iTunesDuration = readString(ITUNES_DURATION)
                ITUNES_IMAGE -> iTunesImageHref = readImageHref(ITUNES_IMAGE)
                ITUNES_EXPLICIT -> iTunesExplicit = readString(ITUNES_EXPLICIT)?.convertYesNo()
                ITUNES_EPISODE -> iTunesEpisode = readString(ITUNES_EPISODE)?.toIntOrNull()
                ITUNES_SEASON -> iTunesSeason = readString(ITUNES_SEASON)?.toIntOrNull()
                ITUNES_EPISODE_TYPE -> iTunesEpisodeType = readString(ITUNES_EPISODE_TYPE)
                ITUNES_BLOCK -> iTunesBlock = readString(ITUNES_BLOCK)?.convertYesNo()

                GOOGLE_DESCRIPTION -> googleDescription = readString(GOOGLE_DESCRIPTION)
                GOOGLE_EXPLICIT -> googleExplicit = readString(GOOGLE_EXPLICIT)?.convertYesNo()
                GOOGLE_BLOCK -> googleBlock = readString(GOOGLE_BLOCK)?.convertYesNo()
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITEM)

        return AutoMixItemData(
            title = title,
            enclosure = enclosure,
            guid = guid,
            pubDate = pubDate,
            description = description ?: googleDescription,
            link = link,
            author = author ?: iTunesAuthor,
            categories = categories.takeIf { it.isNotEmpty() },
            comments = comments,
            source = source,
            simpleTitle = iTunesSimpleTitle,
            duration = iTunesDuration,
            image = iTunesImageHref,
            explicit = iTunesExplicit ?: googleExplicit,
            episode = iTunesEpisode,
            season = iTunesSeason,
            episodeType = iTunesEpisodeType,
            block = iTunesBlock ?: googleBlock
        )
    }

    private fun Image?.replaceInvalidUrlByPriority(vararg priorityHref: String?): Image? {
        if (this == null || priorityHref.isNullOrEmpty() || url != null) return this
        return Image(
            link = link,
            title = title,
            url = priorityHref.find { it != null },
            description = description,
            height = height,
            width = width
        )
    }

    private fun hrefToImage(href: String?): Image? {
        href ?: return null
        return Image(
            link = null,
            title = null,
            url = href,
            description = null,
            height = null,
            width = null
        )
    }
}