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
import tw.ktrssreader.kotlin.constant.ParserConst
import tw.ktrssreader.kotlin.model.channel.Cloud
import tw.ktrssreader.kotlin.model.channel.Image
import tw.ktrssreader.kotlin.model.channel.RssStandardChannel
import tw.ktrssreader.kotlin.model.channel.TextInput
import tw.ktrssreader.kotlin.model.item.Category
import tw.ktrssreader.kotlin.model.item.Enclosure
import tw.ktrssreader.kotlin.model.item.Guid
import tw.ktrssreader.kotlin.model.item.Source
import javax.xml.parsers.DocumentBuilderFactory

abstract class ParserBase<out T : RssStandardChannel> : Parser<T> {

    protected inline fun <T> parseChannel(xml: String, action: Element.() -> T): T {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = builder.parse(xml.byteInputStream())
        document.documentElement.normalize()
        val nodeList = document.getElementsByTagName(ParserConst.CHANNEL)
        var result: T? = null

        // It can only have a channel.
        if (nodeList?.length == 1) {
            val element = nodeList.item(0) as? Element
            element?.let {
                result = action(it)
            }
        }
        return result ?: throw IllegalArgumentException("No valid channel tag in the RSS feed.")
    }

    protected fun Element.readCategories(parentTag: String): List<Category> {
        val result = mutableListOf<Category>()
        val nodeList = getElementsByTagName(ParserConst.CATEGORY) ?: return result

        for (i in 0 until nodeList.length) {
            val e = nodeList.item(i) as? Element ?: continue
            val parent = e.parentNode as? DeferredElementImpl
            if (parent?.tagName != parentTag) continue

            val domain: String? = e.getAttributeOrNull(ParserConst.DOMAIN)
            val name: String? = e.textContent
            result.add(Category(name = name, domain = domain))
        }
        return result
    }

    protected fun Element.readCloud(): Cloud? {
        val element = getElementByTag(ParserConst.CLOUD) ?: return null

        val domain: String? = element.getAttributeOrNull(ParserConst.DOMAIN)
        val port: Int? = element.getAttributeOrNull(ParserConst.PORT)?.toIntOrNull()
        val path: String? = element.getAttributeOrNull(ParserConst.PATH)
        val registerProcedure: String? = element.getAttributeOrNull(ParserConst.REGISTER_PROCEDURE)
        val protocol: String? = element.getAttributeOrNull(ParserConst.PROTOCOL)

        return Cloud(
            domain = domain,
            port = port,
            path = path,
            registerProcedure = registerProcedure,
            protocol = protocol
        )
    }

    protected fun Element.readTextInput(): TextInput? {
        val element = getElementByTag(ParserConst.TEXT_INPUT) ?: return null

        val title: String? = element.readString(ParserConst.TITLE)
        val description: String? = element.readString(name = ParserConst.DESCRIPTION, parentTag = ParserConst.TEXT_INPUT)
        val name: String? = element.readString(ParserConst.NAME)
        val link: String? = element.readString(ParserConst.LINK)
        return TextInput(title = title, description = description, name = name, link = link)
    }

    protected fun Element.readSkipHours(): List<Int>? {
        val element = getElementByTag(ParserConst.SKIP_HOURS) ?: return null

        val hours = mutableListOf<Int>()
        val nodes = element.getElementsByTagName(ParserConst.HOUR)
        for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? Element
            e?.textContent?.toIntOrNull()?.let { hours.add(it) }
        }
        return hours
    }

    protected fun Element.readSkipDays(): List<String>? {
        val element = getElementByTag(ParserConst.SKIP_DAYS) ?: return null

        val days = mutableListOf<String>()
        val nodes = element.getElementsByTagName(ParserConst.DAY)
        for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? Element
            e?.textContent?.let { days.add(it) }
        }
        return days
    }

    protected fun Element.readEnclosure(): Enclosure? {
        val element = getElementByTag(ParserConst.ENCLOSURE) ?: return null

        val url = element.getAttributeOrNull(ParserConst.URL)
        val length = element.getAttributeOrNull(ParserConst.LENGTH)?.toLongOrNull()
        val type = element.getAttributeOrNull(ParserConst.TYPE)
        return Enclosure(url = url, length = length, type = type)
    }

    protected fun Element.readGuid(): Guid? {
        val element = getElementByTag(ParserConst.GUID) ?: return null

        val isPermaLink = element.getAttributeOrNull(ParserConst.PERMALINK)?.toBoolean()
        val value = readString(ParserConst.GUID)
        return Guid(value = value, isPermaLink = isPermaLink)
    }

    protected fun Element.readSource(): Source? {
        val element = getElementByTag(ParserConst.SOURCE) ?: return null

        val url = element.getAttributeOrNull(ParserConst.URL)
        val title = readString(ParserConst.SOURCE)
        return Source(title = title, url = url)
    }

    protected fun Element.readString(name: String, parentTag: String? = null): String? {
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

    protected fun Element.getAttributeOrNull(tag: String): String? {
        val attr = getAttribute(tag)
        return if (attr.isEmpty() || attr.isBlank()) null else attr
    }

    protected fun Element.getElementByTag(tag: String): Element? {
        val nodeList = getElementsByTagName(tag)
        if (nodeList.length == 0) return null
        return nodeList.item(0) as? Element
    }

    protected fun String.toBoolOrNull(): Boolean? {
        return when (toLowerCase()) {
            "yes", "true" -> true
            "no", "false" -> false
            else -> null
        }
    }
}