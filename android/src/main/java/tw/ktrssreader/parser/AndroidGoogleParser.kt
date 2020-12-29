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
import tw.ktrssreader.kotlin.constant.ParserConst
import tw.ktrssreader.kotlin.constant.ParserConst.CHANNEL
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_BLOCK
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_EMAIL
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_EXPLICIT
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_IMAGE
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_OWNER
import tw.ktrssreader.kotlin.constant.ParserConst.ITEM
import tw.ktrssreader.kotlin.model.channel.GoogleChannelData
import tw.ktrssreader.kotlin.model.channel.Image
import tw.ktrssreader.kotlin.model.channel.Owner
import tw.ktrssreader.kotlin.model.channel.RssStandardChannelData
import tw.ktrssreader.kotlin.model.item.Category
import tw.ktrssreader.kotlin.model.item.GoogleItemData
import tw.ktrssreader.kotlin.model.item.RssStandardItem
import tw.ktrssreader.utils.logD
import java.io.IOException

class AndroidGoogleParser : AndroidParserBase<GoogleChannelData>() {

    override val logTag: String = AndroidGoogleParser::class.java.simpleName

    override fun parse(xml: String) = parserGoogleChannel(xml)

    private fun parserGoogleChannel(xml: String): GoogleChannelData {
        val standardChannel = parseStandardChannel(xml)
        return parseChannel(xml) { readGoogleChannel(standardChannel) }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readGoogleChannel(standardChannel: RssStandardChannelData): GoogleChannelData {
        require(XmlPullParser.START_TAG, null, CHANNEL)
        logD(logTag, "[readGoogleChannel]: Reading Google Play channel")
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
                GOOGLE_EXPLICIT -> explicit = readString(GOOGLE_EXPLICIT)?.toBoolOrNull()
                GOOGLE_CATEGORY -> categories = readCategory()
                GOOGLE_AUTHOR -> author = readString(GOOGLE_AUTHOR)
                GOOGLE_OWNER -> owner = Owner(name = null, email = readString(GOOGLE_OWNER))
                GOOGLE_BLOCK -> block = readString(GOOGLE_BLOCK)?.toBoolOrNull()
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
        logD(logTag, "[readItem]: Reading Google Play item")
        var description: String? = null
        var explicit: Boolean? = null
        var block: Boolean? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                GOOGLE_DESCRIPTION -> description = readString(GOOGLE_DESCRIPTION)
                GOOGLE_EXPLICIT -> explicit = readString(GOOGLE_EXPLICIT)?.toBoolOrNull()
                GOOGLE_BLOCK -> block = readString(GOOGLE_BLOCK)?.toBoolOrNull()
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
        logD(logTag, "[readCategory]: categories = $categories")
        return categories
    }
}