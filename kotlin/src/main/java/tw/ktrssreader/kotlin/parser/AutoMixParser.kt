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
import tw.ktrssreader.kotlin.constant.ParserConst.AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.BLOCK
import tw.ktrssreader.kotlin.constant.ParserConst.CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.CHANNEL
import tw.ktrssreader.kotlin.constant.ParserConst.CLOUD
import tw.ktrssreader.kotlin.constant.ParserConst.COMMENTS
import tw.ktrssreader.kotlin.constant.ParserConst.COPYRIGHT
import tw.ktrssreader.kotlin.constant.ParserConst.DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.DOCS
import tw.ktrssreader.kotlin.constant.ParserConst.EMAIL
import tw.ktrssreader.kotlin.constant.ParserConst.EXPLICIT
import tw.ktrssreader.kotlin.constant.ParserConst.GENERATOR
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_BLOCK
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_EMAIL
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_EXPLICIT
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_IMAGE
import tw.ktrssreader.kotlin.constant.ParserConst.HREF
import tw.ktrssreader.kotlin.constant.ParserConst.IMAGE
import tw.ktrssreader.kotlin.constant.ParserConst.ITEM
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_BLOCK
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_COMPLETE
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_DURATION
import tw.ktrssreader.kotlin.constant.ParserConst.ITUNES_EMAIL
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
import tw.ktrssreader.kotlin.constant.ParserConst.OWNER
import tw.ktrssreader.kotlin.constant.ParserConst.PUB_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.RATING
import tw.ktrssreader.kotlin.constant.ParserConst.SKIP_DAYS
import tw.ktrssreader.kotlin.constant.ParserConst.SKIP_HOURS
import tw.ktrssreader.kotlin.constant.ParserConst.TEXT_INPUT
import tw.ktrssreader.kotlin.constant.ParserConst.TITLE
import tw.ktrssreader.kotlin.constant.ParserConst.TTL
import tw.ktrssreader.kotlin.constant.ParserConst.TYPE
import tw.ktrssreader.kotlin.constant.ParserConst.WEB_MASTER
import tw.ktrssreader.kotlin.extension.hrefToImage
import tw.ktrssreader.kotlin.extension.replaceInvalidUrlByPriority
import tw.ktrssreader.kotlin.model.channel.*
import tw.ktrssreader.kotlin.model.item.*

class AutoMixParser : ParserBase<AutoMixChannel>() {

    private val rssStandardMap = HashMap<String, Any?>(20)
    private val iTunesMap = HashMap<String, Any?>(11)
    private val googlePlayMap = HashMap<String, Any?>(8)

    override fun parse(xml: String): AutoMixChannel {
        return parseChannel(xml) {
            rssStandardMap.clear()
            iTunesMap.clear()
            googlePlayMap.clear()
            parseChannelTags()

            AutoMixChannelData(
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
    }

    /**
     * The priority of reading tags as the following sequence:
     * RssStandard -> iTunes -> GooglePlay
     */
    private fun <R> priorityMapValueOf(key: String): R? {
        @Suppress("UNCHECKED_CAST")
        return (rssStandardMap[key] ?: iTunesMap[key] ?: googlePlayMap[key]) as? R
    }

    private fun Element.parseChannelTags() {
        val title: String? = readString(TITLE)
        val description: String? = readString(name = DESCRIPTION, parentTag = CHANNEL)
        val image: Image? = readImage()
        val language: String? = readString(LANGUAGE)
        val categories = readCategories(CHANNEL)
        val link: String? = readString(LINK)
        val copyright: String? = readString(COPYRIGHT)
        val managingEditor: String? = readString(MANAGING_EDITOR)
        val webMaster: String? = readString(WEB_MASTER)
        val pubDate: String? = readString(PUB_DATE)
        val lastBuildDate: String? = readString(LAST_BUILD_DATE)
        val generator: String? = readString(GENERATOR)
        val docs: String? = readString(DOCS)
        val cloud: Cloud? = readCloud()
        val ttl: Int? = readString(TTL)?.toIntOrNull()
        val rating: String? = readString(RATING)
        val textInput: TextInput? = readTextInput()
        val skipHours: List<Int>? = readSkipHours()
        val skipDays: List<String>? = readSkipDays()
        val items = readItems()

        val iTunesImageHref: String? = getElementByTag(ITUNES_IMAGE)?.getAttributeOrNull(HREF)
        val iTunesCategories: List<Category>? = readCategories(
            parentTag = CHANNEL,
            tagName = ITUNES_CATEGORY
        )
        val iTunesSimpleTitle: String? = readString(ITUNES_TITLE)
        val iTunesExplicit: Boolean? = readString(ITUNES_EXPLICIT)?.toBoolean()
        val iTunesEmail: String? = readString(name = ITUNES_EMAIL, parentTag = CHANNEL)
        val iTunesAuthor: String? = readString(ITUNES_AUTHOR)
        val iTunesOwner: Owner? = readITunesOwner()
        val iTunesType: String? = readString(ITUNES_TYPE)
        val iTunesNewFeedUrl: String? = readString(ITUNES_NEW_FEED_URL)
        val iTunesBlock: Boolean? = readString(ITUNES_BLOCK)?.toBoolOrNull()
        val iTunesComplete: Boolean? = readString(ITUNES_COMPLETE)?.toBoolOrNull()

        val googleDescription: String? = readString(GOOGLE_DESCRIPTION)
        val googleImageHref: String? = getElementByTag(GOOGLE_IMAGE)?.getAttributeOrNull(HREF)
        val googleCategories: List<Category>? =
            readCategories(parentTag = CHANNEL, tagName = GOOGLE_CATEGORY)
        val googleExplicit: Boolean? = readString(GOOGLE_EXPLICIT)?.toBoolOrNull()
        val googleEmail: String? = readString(name = GOOGLE_EMAIL, parentTag = CHANNEL)
        val googleAuthor: String? = readString(GOOGLE_AUTHOR)
        val googleOwner: Owner? = readGoogleOwner()
        val googleBlock: Boolean? = readString(GOOGLE_BLOCK)?.toBoolOrNull()

        rssStandardMap.run {
            put(TITLE, title)
            put(DESCRIPTION, description)
            put(
                IMAGE,
                image.replaceInvalidUrlByPriority(iTunesImageHref, googleImageHref)
            )
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
            put(IMAGE, iTunesImageHref.hrefToImage())
            put(CATEGORY, iTunesCategories?.takeIf { it.isNotEmpty() })
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
            put(IMAGE, googleImageHref.hrefToImage())
            put(CATEGORY, googleCategories?.takeIf { it.isNotEmpty() })
            put(EXPLICIT, googleExplicit)
            put(EMAIL, googleEmail)
            put(AUTHOR, googleAuthor)
            put(OWNER, googleOwner)
            put(BLOCK, googleBlock)
        }
    }

    private fun Element.readItems(): List<AutoMixItemData> {
        val result = mutableListOf<AutoMixItemData>()
        val nodeList = getElementsByTagName(ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            element.run {
                val title: String? = readString(name = TITLE, parentTag = ITEM)
                val enclosure: Enclosure? = readEnclosure()
                val guid: Guid? = readGuid()
                val pubDate: String? = readString(PUB_DATE)
                val description: String? = readString(name = DESCRIPTION, parentTag = ITEM)
                val link: String? = readString(LINK, parentTag = ITEM)
                val author: String? = readString(AUTHOR)
                val categories = readCategories(ITEM)
                val comments: String? = readString(COMMENTS)
                val source: Source? = readSource()

                val iTunesAuthor: String? = readString(ITUNES_AUTHOR)
                val iTunesSimpleTitle: String? = readString(ITUNES_TITLE)
                val iTunesDuration: String? = readString(ITUNES_DURATION)
                val iTunesImageHref: String? = getElementByTag(ITUNES_IMAGE)?.getAttributeOrNull(HREF)
                val iTunesExplicit: Boolean? = readString(ITUNES_EXPLICIT)?.toBoolOrNull()
                val iTunesEpisode: Int? = readString(ITUNES_EPISODE)?.toIntOrNull()
                val iTunesSeason: Int? = readString(ITUNES_SEASON)?.toIntOrNull()
                val iTunesEpisodeType: String? = readString(ITUNES_EPISODE_TYPE)
                val iTunesBlock: Boolean? = readString(ITUNES_BLOCK)?.toBoolOrNull()

                val googleDescription: String? = readString(GOOGLE_DESCRIPTION)
                val googleExplicit: Boolean? = readString(GOOGLE_EXPLICIT)?.toBoolOrNull()
                val googleBlock: Boolean? = readString(GOOGLE_BLOCK)?.toBoolOrNull()

                result.add(
                    AutoMixItemData(
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
                )
            }
        }
        return result
    }
}