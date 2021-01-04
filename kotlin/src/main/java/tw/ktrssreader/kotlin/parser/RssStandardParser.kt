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

package tw.ktrssreader.kotlin.parser

import org.w3c.dom.Element
import tw.ktrssreader.kotlin.constant.ParserConst
import tw.ktrssreader.kotlin.constant.ParserConst.CHANNEL
import tw.ktrssreader.kotlin.constant.ParserConst.COPYRIGHT
import tw.ktrssreader.kotlin.constant.ParserConst.DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.DOCS
import tw.ktrssreader.kotlin.constant.ParserConst.GENERATOR
import tw.ktrssreader.kotlin.constant.ParserConst.LANGUAGE
import tw.ktrssreader.kotlin.constant.ParserConst.LAST_BUILD_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.LINK
import tw.ktrssreader.kotlin.constant.ParserConst.MANAGING_EDITOR
import tw.ktrssreader.kotlin.constant.ParserConst.PUB_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.RATING
import tw.ktrssreader.kotlin.constant.ParserConst.TITLE
import tw.ktrssreader.kotlin.constant.ParserConst.TTL
import tw.ktrssreader.kotlin.constant.ParserConst.WEB_MASTER
import tw.ktrssreader.kotlin.model.channel.Image
import tw.ktrssreader.kotlin.model.channel.RssStandardChannel
import tw.ktrssreader.kotlin.model.channel.RssStandardChannelData
import tw.ktrssreader.kotlin.model.item.RssStandardItemData

class RssStandardParser : ParserBase<RssStandardChannel>() {

    override fun parse(xml: String): RssStandardChannel {
        return parseChannel(xml) {
            val title = readString(TITLE)
            val description = readString(name = DESCRIPTION, parentTag = CHANNEL)
            val link = readString(LINK)
            val image = readImage()
            val language = readString(LANGUAGE)
            val categories = readCategories(CHANNEL) ?: listOf()
            val copyright = readString(COPYRIGHT)
            val managingEditor = readString(MANAGING_EDITOR)
            val webMaster = readString(WEB_MASTER)
            val pubDate = readString(PUB_DATE)
            val lastBuildDate = readString(LAST_BUILD_DATE)
            val generator = readString(GENERATOR)
            val docs = readString(DOCS)
            val cloud = readCloud()
            val ttl = readString(TTL)?.toIntOrNull()
            val rating = readString(RATING)
            val textInput = readTextInput()
            val skipHours = readSkipHours()
            val skipDays = readSkipDays()
            val items = readItems() ?: listOf()

            RssStandardChannelData(
                title = title,
                description = description,
                image = image,
                language = language,
                categories = if (categories.isEmpty()) null else categories,
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
                items = if (items.isEmpty()) null else items
            )
        }
    }

    private fun Element.readItems(): List<RssStandardItemData> {
        val result = mutableListOf<RssStandardItemData>()
        val nodeList = getElementsByTagName(ParserConst.ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            val title = element.readString(ParserConst.TITLE)
            val enclosure = element.readEnclosure()
            val guid = element.readGuid()
            val pubDate = element.readString(ParserConst.PUB_DATE)
            val description = element.readString(ParserConst.DESCRIPTION)
            val link = element.readString(ParserConst.LINK)
            val author = element.readString(ParserConst.AUTHOR)
            val categories = element.readCategories(ParserConst.ITEM)
            val comments = element.readString(ParserConst.COMMENTS)
            val source = element.readSource()
            result.add(
                RssStandardItemData(
                    title = title,
                    enclosure = enclosure,
                    guid = guid,
                    pubDate = pubDate,
                    description = description,
                    link = link,
                    author = author,
                    categories = if (categories.isEmpty()) null else categories,
                    comments = comments,
                    source = source
                )
            )
        }
        return result
    }

    private fun Element.readImage(): Image? {
        val element = getElementByTag(ParserConst.IMAGE) ?: return null

        val link = element.readString(ParserConst.LINK)
        val title = element.readString(ParserConst.TITLE)
        val url = element.readString(ParserConst.URL)
        val description = element.readString(ParserConst.DESCRIPTION)
        val height = element.readString(ParserConst.HEIGHT)?.toIntOrNull()
        val width = element.readString(ParserConst.WIDTH)?.toIntOrNull()
        return Image(
            link = link,
            title = title,
            url = url,
            description = description,
            height = height,
            width = width
        )
    }
}
