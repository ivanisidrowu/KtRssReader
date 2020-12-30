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
import tw.ktrssreader.kotlin.constant.ParserConst.CATEGORY
import tw.ktrssreader.kotlin.constant.ParserConst.CHANNEL
import tw.ktrssreader.kotlin.constant.ParserConst.CLOUD
import tw.ktrssreader.kotlin.constant.ParserConst.COMMENTS
import tw.ktrssreader.kotlin.constant.ParserConst.COPYRIGHT
import tw.ktrssreader.kotlin.constant.ParserConst.DAY
import tw.ktrssreader.kotlin.constant.ParserConst.DESCRIPTION
import tw.ktrssreader.kotlin.constant.ParserConst.DOCS
import tw.ktrssreader.kotlin.constant.ParserConst.DOMAIN
import tw.ktrssreader.kotlin.constant.ParserConst.ENCLOSURE
import tw.ktrssreader.kotlin.constant.ParserConst.GENERATOR
import tw.ktrssreader.kotlin.constant.ParserConst.GUID
import tw.ktrssreader.kotlin.constant.ParserConst.HEIGHT
import tw.ktrssreader.kotlin.constant.ParserConst.HOUR
import tw.ktrssreader.kotlin.constant.ParserConst.IMAGE
import tw.ktrssreader.kotlin.constant.ParserConst.ITEM
import tw.ktrssreader.kotlin.constant.ParserConst.LANGUAGE
import tw.ktrssreader.kotlin.constant.ParserConst.LAST_BUILD_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.LENGTH
import tw.ktrssreader.kotlin.constant.ParserConst.LINK
import tw.ktrssreader.kotlin.constant.ParserConst.MANAGING_EDITOR
import tw.ktrssreader.kotlin.constant.ParserConst.NAME
import tw.ktrssreader.kotlin.constant.ParserConst.PATH
import tw.ktrssreader.kotlin.constant.ParserConst.PERMALINK
import tw.ktrssreader.kotlin.constant.ParserConst.PORT
import tw.ktrssreader.kotlin.constant.ParserConst.PROTOCOL
import tw.ktrssreader.kotlin.constant.ParserConst.PUB_DATE
import tw.ktrssreader.kotlin.constant.ParserConst.RATING
import tw.ktrssreader.kotlin.constant.ParserConst.REGISTER_PROCEDURE
import tw.ktrssreader.kotlin.constant.ParserConst.SKIP_DAYS
import tw.ktrssreader.kotlin.constant.ParserConst.SKIP_HOURS
import tw.ktrssreader.kotlin.constant.ParserConst.SOURCE
import tw.ktrssreader.kotlin.constant.ParserConst.TEXT_INPUT
import tw.ktrssreader.kotlin.constant.ParserConst.TITLE
import tw.ktrssreader.kotlin.constant.ParserConst.TTL
import tw.ktrssreader.kotlin.constant.ParserConst.TYPE
import tw.ktrssreader.kotlin.constant.ParserConst.URL
import tw.ktrssreader.kotlin.constant.ParserConst.WEB_MASTER
import tw.ktrssreader.kotlin.constant.ParserConst.WIDTH
import tw.ktrssreader.kotlin.model.channel.*
import tw.ktrssreader.kotlin.model.item.*
import javax.xml.parsers.DocumentBuilderFactory

class RssStandardParser : Parser<RssStandardChannel> {

    override fun parse(xml: String): RssStandardChannel {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = builder.parse(xml.byteInputStream())
        document.documentElement.normalize()
        val nodeList = document.getElementsByTagName(CHANNEL)
        var result: RssStandardChannel? = null


        // It can only have a channel.
        if (nodeList.length == 1) {
            val element = nodeList.item(0) as? Element
            val title = element?.readString(TITLE)
            val description = element?.readString(name = DESCRIPTION, parentTag = CHANNEL)
            val link = element?.readString(LINK)
            val image = element?.readImage()
            val language = element?.readString(LANGUAGE)
            val categories = element?.readCategories(CHANNEL) ?: listOf()
            val copyright = element?.readString(COPYRIGHT)
            val managingEditor = element?.readString(MANAGING_EDITOR)
            val webMaster = element?.readString(WEB_MASTER)
            val pubDate = element?.readString(PUB_DATE)
            val lastBuildDate = element?.readString(LAST_BUILD_DATE)
            val generator = element?.readString(GENERATOR)
            val docs = element?.readString(DOCS)
            val cloud = element?.readCloud()
            val ttl = element?.readString(TTL)?.toIntOrNull()
            val rating = element?.readString(RATING)
            val textInput = element?.readTextInput()
            val skipHours = element?.readSkipHours()
            val skipDays = element?.readSkipDays()
            val items = element?.readItems() ?: listOf()

            result = RssStandardChannelData(
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
        return result ?: throw IllegalArgumentException("No valid channel tag in the RSS feed.")
    }

    private fun Element.readString(name: String, parentTag: String? = null): String? {
        val nodeList = getElementsByTagName(name)
        if (parentTag == null) {
            return nodeList.item(0)?.textContent
        } else {
            for (i in 0 until nodeList.length) {
                val e = nodeList.item(i) as? Element ?: continue
                val parent = e.parentNode as? DeferredElementImpl
                if (parent?.tagName != parentTag) continue

                return e.textContent
            }
            return null
        }
    }

    private fun Element.readCategories(parentTag: String): MutableList<Category> {
        val result = mutableListOf<Category>()
        val nodeList = getElementsByTagName(CATEGORY) ?: return result

        for (i in 0 until nodeList.length) {
            val e = nodeList.item(i) as? Element ?: continue
            val parent = e.parentNode as? DeferredElementImpl
            if (parent?.tagName != parentTag) continue

            val domain: String? = e.getAttributeOrNull(DOMAIN)
            val name: String? = e.textContent
            result.add(Category(name = name, domain = domain))
        }
        return result
    }

    private fun Element.readCloud(): Cloud? {
        val element = getElementByTag(CLOUD) ?: return null

        val domain: String? = element.getAttributeOrNull(DOMAIN)
        val port: Int? = element.getAttributeOrNull(PORT)?.toIntOrNull()
        val path: String? = element.getAttributeOrNull(PATH)
        val registerProcedure: String? = element.getAttributeOrNull(REGISTER_PROCEDURE)
        val protocol: String? = element.getAttributeOrNull(PROTOCOL)

        return Cloud(
            domain = domain,
            port = port,
            path = path,
            registerProcedure = registerProcedure,
            protocol = protocol
        )
    }

    private fun Element.readTextInput(): TextInput? {
        val element = getElementByTag(TEXT_INPUT) ?: return null

        val title: String? = element.readString(TITLE)
        val description: String? = element.readString(name = DESCRIPTION, parentTag = TEXT_INPUT)
        val name: String? = element.readString(NAME)
        val link: String? = element.readString(LINK)
        return TextInput(title = title, description = description, name = name, link = link)
    }

    private fun Element.readSkipHours(): List<Int>? {
        val element = getElementByTag(SKIP_HOURS) ?: return null

        val hours = mutableListOf<Int>()
        val nodes = element.getElementsByTagName(HOUR)
        for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? Element
            e?.textContent?.toIntOrNull()?.let { hours.add(it) }
        }
        return hours
    }

    private fun Element.readSkipDays(): List<String>? {
        val element = getElementByTag(SKIP_DAYS) ?: return null

        val days = mutableListOf<String>()
        val nodes = element.getElementsByTagName(DAY)
        for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? Element
            e?.textContent?.let { days.add(it) }
        }
        return days
    }

    private fun Element.readImage(): Image? {
        val element = getElementByTag(IMAGE) ?: return null

        val link = element.readString(LINK)
        val title = element.readString(TITLE)
        val url = element.readString(URL)
        val description = element.readString(DESCRIPTION)
        val height = element.readString(HEIGHT)?.toIntOrNull()
        val width = element.readString(WIDTH)?.toIntOrNull()
        return Image(
            link = link,
            title = title,
            url = url,
            description = description,
            height = height,
            width = width
        )
    }

    private fun Element.readItems(): List<RssStandardItemData> {
        val result = mutableListOf<RssStandardItemData>()
        val nodeList = getElementsByTagName(ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            val title = element.readString(TITLE)
            val enclosure = element.readEnclosure()
            val guid = element.readGuid()
            val pubDate = element.readString(PUB_DATE)
            val description = element.readString(DESCRIPTION)
            val link = element.readString(LINK)
            val author = element.readString(AUTHOR)
            val categories = element.readCategories(ITEM)
            val comments = element.readString(COMMENTS)
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

    private fun Element.readEnclosure(): Enclosure? {
        val element = getElementByTag(ENCLOSURE) ?: return null

        val url = element.getAttributeOrNull(URL)
        val length = element.getAttributeOrNull(LENGTH)?.toLongOrNull()
        val type = element.getAttributeOrNull(TYPE)
        return Enclosure(url = url, length = length, type = type)
    }

    private fun Element.readGuid(): Guid? {
        val element = getElementByTag(GUID) ?: return null

        val isPermaLink = element.getAttributeOrNull(PERMALINK)?.toBoolean()
        val value = readString(GUID)
        return Guid(value = value, isPermaLink = isPermaLink)
    }

    private fun Element.readSource(): Source? {
        val element = getElementByTag(SOURCE) ?: return null

        val url = element.getAttributeOrNull(URL)
        val title = readString(SOURCE)
        return Source(title = title, url = url)
    }

    private fun Element.getAttributeOrNull(tag: String): String? {
        val attr = getAttribute(tag)
        return if (attr.isEmpty() || attr.isBlank()) null else attr
    }

    private fun Element.getElementByTag(tag: String): Element? {
        val nodeList = getElementsByTagName(tag)
        if (nodeList.length == 0) return null
        return nodeList.item(0) as? Element
    }
}
