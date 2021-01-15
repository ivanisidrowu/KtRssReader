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
import tw.ktrssreader.kotlin.constant.ParserConst.CHANNEL
import tw.ktrssreader.kotlin.constant.ParserConst.COMMENTS
import tw.ktrssreader.kotlin.constant.ParserConst.COPYRIGHT
import tw.ktrssreader.kotlin.constant.ParserConst.DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.DOCS
import tw.ktrssreader.kotlin.constant.ParserConst.GENERATOR
import tw.ktrssreader.kotlin.constant.ParserConst.HREF
import tw.ktrssreader.kotlin.constant.ParserConst.ITEM
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_BLOCK
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_COMPLETE
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_DURATION
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_EPISODE
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_EPISODE_TYPE
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_EXPLICIT
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_IMAGE
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_NEW_FEED_URL
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_SEASON
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_TITLE
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_TYPE
import tw.ktrssreader.kotlin.constant.ParserConst.LANGUAGE
import tw.ktrssreader.kotlin.constant.ParserConst.LAST_BUILD_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.LINK
import tw.ktrssreader.kotlin.constant.ParserConst.MANAGING_EDITOR
import tw.ktrssreader.kotlin.constant.ParserConst.PUB_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.RATING
import tw.ktrssreader.kotlin.constant.ParserConst.TITLE
import tw.ktrssreader.kotlin.constant.ParserConst.TTL
import tw.ktrssreader.kotlin.constant.ParserConst.WEB_MASTER
import tw.ktrssreader.kotlin.model.channel.ITunesChannelData
import tw.ktrssreader.kotlin.model.channel.Image
import tw.ktrssreader.kotlin.model.channel.Owner
import tw.ktrssreader.kotlin.model.item.Category
import tw.ktrssreader.kotlin.model.item.ITunesItemData

class ITunesParser : ParserBase<ITunesChannelData>() {

    override fun parse(xml: String): ITunesChannelData {
        return parseChannel(xml) {
            val title = readString(TITLE)
            val description = readString(name = DESCRIPTION, parentTag = CHANNEL)
            val link = readString(LINK)
            val language = readString(LANGUAGE)
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

            val image: Image? = readITunesImage()
            val explicit: Boolean? = readString(ITUNES_EXPLICIT)?.toBoolean()
            val categories: List<Category>? =
                readCategories(parentTag = CHANNEL, tagName = ITUNES_CATEGORY)
            val author: String? = readString(ITUNES_AUTHOR)
            val owner: Owner? = readITunesOwner()
            val simpleTitle: String? = readString(ITUNES_TITLE)
            val type: String? = readString(ITUNES_TYPE)
            val newFeedUrl: String? = readString(ITUNES_NEW_FEED_URL)
            val block: Boolean? = readString(ITUNES_COMPLETE)?.toBoolOrNull()
            val complete: Boolean? = readString(ITUNES_COMPLETE)?.toBoolOrNull()
            val items: List<ITunesItemData> = readItems()

            ITunesChannelData(
                title = title,
                description = description,
                image = image,
                language = language,
                categories = categories?.takeIf { it.isNotEmpty() },
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
    }

    private fun Element.readITunesImage(): Image? {
        val element = getElementByTag(ITUNES_IMAGE) ?: return null
        val href = element.getAttributeOrNull(HREF)
        return Image(
            link = null,
            title = null,
            url = href,
            description = null,
            height = null,
            width = null
        )
    }

    private fun Element.readItems(): List<ITunesItemData> {
        val result = mutableListOf<ITunesItemData>()
        val nodeList = getElementsByTagName(ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            val title = element.readString(TITLE)
            val enclosure = element.readEnclosure()
            val guid = element.readGuid()
            val pubDate = element.readString(PUB_DATE)
            val description = element.readString(DESCRIPTION)
            val link = element.readString(LINK)
            val categories = element.readCategories(ITEM)
            val comments = element.readString(COMMENTS)
            val source = element.readSource()

            val simpleTitle: String? = element.readString(ITUNES_TITLE)
            val duration: String? = element.readString(ITUNES_DURATION)
            val image: String? = element.getElementByTag(ITUNES_IMAGE)?.getAttributeOrNull(HREF)
            val explicit: Boolean? = element.readString(ITUNES_EXPLICIT)?.toBoolOrNull()
            val episode: Int? = element.readString(ITUNES_EPISODE)?.toIntOrNull()
            val season: Int? = element.readString(ITUNES_SEASON)?.toIntOrNull()
            val episodeType: String? = element.readString(ITUNES_EPISODE_TYPE)
            val block: Boolean? = element.readString(ITUNES_BLOCK)?.toBoolOrNull()
            val author: String? = element.readString(ITUNES_AUTHOR)

            result.add(
                ITunesItemData(
                    title = title,
                    enclosure = enclosure,
                    guid = guid,
                    pubDate = pubDate,
                    description = description,
                    link = link,
                    author = author,
                    categories = categories.takeIf { it.isNotEmpty() },
                    comments = comments,
                    source = source,
                    simpleTitle = simpleTitle,
                    duration = duration,
                    image = image,
                    explicit = explicit,
                    episode = episode,
                    season = season,
                    episodeType = episodeType,
                    block = block
                )
            )
        }
        return result
    }
}
