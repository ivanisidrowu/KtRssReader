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
import tw.ktrssreader.constant.ParserConst.CHANNEL
import tw.ktrssreader.constant.ParserConst.HREF
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
import tw.ktrssreader.constant.ParserConst.TEXT
import tw.ktrssreader.model.channel.ITunesChannelData
import tw.ktrssreader.model.channel.Image
import tw.ktrssreader.model.channel.Owner
import tw.ktrssreader.model.channel.RssStandardChannelData
import tw.ktrssreader.model.item.Category
import tw.ktrssreader.model.item.ITunesItemData
import tw.ktrssreader.model.item.RssStandardItem
import tw.ktrssreader.utils.logD
import java.io.IOException

class ITunesParser : ParserBase<ITunesChannelData>() {

    override val logTag: String = ITunesParser::class.java.simpleName

    override fun parse(xml: String) = parseITunesChannel(xml)

    private fun parseITunesChannel(xml: String): ITunesChannelData {
        val standardChannel = parseStandardChannel(xml)
        return parseChannel(xml) { readITunesChannel(standardChannel) }
    }

    private fun XmlPullParser.readITunesChannel(standardChannel: RssStandardChannelData): ITunesChannelData {
        require(XmlPullParser.START_TAG, null, CHANNEL)
        logD(logTag, "[readITunesChannel]: Reading iTunes channel")
        var image: Image? = null
        var explicit: Boolean? = null
        var categories: List<Category>? = null
        var author: String? = null
        var owner: Owner? = null
        var simpleTitle: String? = null
        var type: String? = null
        var newFeedUrl: String? = null
        var block: Boolean? = null
        var complete: Boolean? = null
        val items: MutableList<ITunesItemData> = mutableListOf()
        var itemIndex = 0

        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_IMAGE -> image = readImage()
                ITUNES_EXPLICIT -> explicit = readString(ITUNES_EXPLICIT)?.toBoolean()
                ITUNES_CATEGORY -> categories = readCategory()
                ITUNES_AUTHOR -> author = readString(ITUNES_AUTHOR)
                ITUNES_OWNER -> owner = readOwner()
                ITUNES_TITLE -> simpleTitle = readString(ITUNES_TITLE)
                ITUNES_TYPE -> type = readString(ITUNES_TYPE)
                ITUNES_NEW_FEED_URL -> newFeedUrl = readString(ITUNES_NEW_FEED_URL)
                ITUNES_BLOCK -> block = readString(ITUNES_BLOCK)?.toBoolOrNull()
                ITUNES_COMPLETE -> complete = readString(ITUNES_COMPLETE)?.toBoolOrNull()
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
        return ITunesChannelData(
            title = standardChannel.title,
            description = standardChannel.description,
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
            simpleTitle = simpleTitle,
            explicit = explicit,
            author = author,
            owner = owner,
            type = type,
            newFeedUrl = newFeedUrl,
            block = block,
            complete = complete
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readImage(): Image {
        require(XmlPullParser.START_TAG, null, ITUNES_IMAGE)
        val href: String? = getAttributeValue(null, HREF)
        nextTag()
        require(XmlPullParser.END_TAG, null, ITUNES_IMAGE)
        logD(logTag, "[readImage]: href = $href")
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
    private fun XmlPullParser.readCategory(): List<Category>? {
        require(XmlPullParser.START_TAG, null, ITUNES_CATEGORY)
        val categories = mutableListOf<Category>()
        getAttributeValue(null, TEXT)?.let { categories.add(Category(name = it, domain = null)) }
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_CATEGORY -> {
                    getAttributeValue(null, TEXT)
                        ?.let { categories.add(Category(name = it, domain = null)) }
                    nextTag()
                }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITUNES_CATEGORY)
        logD(logTag, "[readCategory]: categories = $categories")
        return categories
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readOwner(): Owner {
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
    private fun XmlPullParser.readItem(standardItem: RssStandardItem): ITunesItemData {
        require(XmlPullParser.START_TAG, null, ITEM)
        logD(logTag, "[readItem]: Reading iTunes item")
        var simpleTitle: String? = null
        var duration: String? = null
        var image: String? = null
        var explicit: Boolean? = null
        var episode: Int? = null
        var season: Int? = null
        var episodeType: String? = null
        var block: Boolean? = null
        var author: String? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_DURATION -> duration = readString(ITUNES_DURATION)
                ITUNES_IMAGE -> image = readImage().url
                ITUNES_EXPLICIT -> explicit = readString(ITUNES_EXPLICIT)?.toBoolean()
                ITUNES_TITLE -> simpleTitle = readString(ITUNES_TITLE)
                ITUNES_EPISODE -> episode = readString(ITUNES_EPISODE)?.toIntOrNull()
                ITUNES_SEASON -> season = readString(ITUNES_SEASON)?.toIntOrNull()
                ITUNES_EPISODE_TYPE -> episodeType = readString(ITUNES_EPISODE_TYPE)
                ITUNES_BLOCK -> block = readString(ITUNES_BLOCK)?.toBoolOrNull()
                ITUNES_AUTHOR -> author = readString(ITUNES_AUTHOR)
                else -> skip()
            }
        }

        require(XmlPullParser.END_TAG, null, ITEM)
        return ITunesItemData(
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
}