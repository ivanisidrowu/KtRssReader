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

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import org.w3c.dom.Element
import tw.ktrssreader.kotlin.constant.ParserConst.AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.CHANNEL
import tw.ktrssreader.kotlin.constant.ParserConst.COMMENTS
import tw.ktrssreader.kotlin.constant.ParserConst.COPYRIGHT
import tw.ktrssreader.kotlin.constant.ParserConst.DOCS
import tw.ktrssreader.kotlin.constant.ParserConst.GENERATOR
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_AUTHOR
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_BLOCK
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_EMAIL
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_EXPLICIT
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_IMAGE
import tw.ktrssreader.kotlin.constant.ParserConst.GOOGLE_OWNER
import tw.ktrssreader.kotlin.constant.ParserConst.HREF
import tw.ktrssreader.kotlin.constant.ParserConst.ITEM
import tw.ktrssreader.kotlin.constant.ParserConst.LANGUAGE
import tw.ktrssreader.kotlin.constant.ParserConst.LAST_BUILD_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.LINK
import tw.ktrssreader.kotlin.constant.ParserConst.MANAGING_EDITOR
import tw.ktrssreader.kotlin.constant.ParserConst.PUB_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.RATING
import tw.ktrssreader.kotlin.constant.ParserConst.TEXT
import tw.ktrssreader.kotlin.constant.ParserConst.TITLE
import tw.ktrssreader.kotlin.constant.ParserConst.TTL
import tw.ktrssreader.kotlin.constant.ParserConst.WEB_MASTER
import tw.ktrssreader.kotlin.model.channel.GoogleChannelData
import tw.ktrssreader.kotlin.model.channel.Image
import tw.ktrssreader.kotlin.model.channel.Owner
import tw.ktrssreader.kotlin.model.item.Category
import tw.ktrssreader.kotlin.model.item.GoogleItemData

class GoogleParser : ParserBase<GoogleChannelData>() {

    override fun parse(xml: String): GoogleChannelData {
        return parseChannel(xml) {
            val title = readString(TITLE)
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
            val items = readItems() ?: listOf()

            val description = readString(name = GOOGLE_DESCRIPTION, parentTag = CHANNEL)
            val image = getElementByTag(GOOGLE_IMAGE)?.readGoogleImage()
            val explicit = readString(GOOGLE_EXPLICIT)?.toBoolOrNull()
            val categories = readGoogleCategories(parentTag = CHANNEL) ?: listOf()
            val author = readString(GOOGLE_AUTHOR)
            val owner = readGoogleOwner()
            val block = readString(GOOGLE_BLOCK)?.toBoolOrNull()
            val email = readString(GOOGLE_EMAIL)

            GoogleChannelData(
                title = title,
                description = description,
                image = image,
                language = language,
                categories = categories.takeIf { it.isNotEmpty() },
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
                explicit = explicit,
                author = author,
                owner = owner,
                block = block,
                email = email
            )
        }
    }

    private fun Element.readGoogleImage(): Image {
        val href = getAttribute(HREF)
        return Image(
            link = null,
            title = null,
            url = href,
            description = null,
            height = null,
            width = null
        )
    }

    private fun Element.readGoogleOwner(): Owner? {
        val nodeList = getElementsByTagName(GOOGLE_OWNER)
        if (nodeList.length == 0) return null

        return Owner(name = null, email = readString(GOOGLE_OWNER))
    }

    private fun Element.readGoogleCategories(parentTag: String): List<Category> {
        val result = mutableListOf<Category>()
        val nodeList = getElementsByTagName(GOOGLE_CATEGORY) ?: return result

        for (i in 0 until nodeList.length) {
            val e = nodeList.item(i) as? Element ?: continue
            val parent = e.parentNode as? DeferredElementImpl
            if (parent?.tagName == parentTag || parent?.tagName == GOOGLE_CATEGORY) {
                val name: String? = e.getAttributeOrNull(TEXT)
                result.add(Category(name = name, domain = null))
            }
        }
        return result
    }

    private fun Element.readItems(): List<GoogleItemData> {
        val result = mutableListOf<GoogleItemData>()
        val nodeList = getElementsByTagName(ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            val title = element.readString(TITLE)
            val enclosure = element.readEnclosure()
            val guid = element.readGuid()
            val pubDate = element.readString(PUB_DATE)
            val link = element.readString(LINK)
            val author = element.readString(AUTHOR)
            val categories = element.readCategories(ITEM)
            val comments = element.readString(COMMENTS)
            val source = element.readSource()

            val googleDescription: String? = element.readString(GOOGLE_DESCRIPTION)
            val googleExplicit: Boolean? = element.readString(GOOGLE_EXPLICIT)?.toBoolOrNull()
            val googleBlock: Boolean? = element.readString(GOOGLE_BLOCK)?.toBoolOrNull()

            result.add(
                GoogleItemData(
                    title = title,
                    enclosure = enclosure,
                    guid = guid,
                    pubDate = pubDate,
                    description = googleDescription,
                    link = link,
                    author = author,
                    categories = categories.takeIf { it.isNotEmpty() },
                    comments = comments,
                    source = source,
                    explicit = googleExplicit,
                    block = googleBlock
                )
            )
        }
        return result
    }
}