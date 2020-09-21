package tw.ktrssreader.model.channel

import tw.ktrssreader.model.item.*
import java.lang.StringBuilder
import java.io.Serializable

open class RssStandardChannel(
    open val title: String?,
    open val description: String?,
    open val image: Image?,
    open val language: String?,
    open val categories: List<Category>?,
    open val link: String?,
    open val copyright: String?,
    open val managingEditor: String?,
    open val webMaster: String?,
    open val pubDate: String?,
    open val lastBuildDate: String?,
    open val generator: String?,
    open val docs: String?,
    open val cloud: Cloud?,
    open val ttl: Int?,
    open val rating: String?,
    open val textInput: TextInput?,
    open val skipHours: List<Int>?,
    open val skipDays: List<String>?,
    open val items: List<RssStandardItem>?,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RssStandardChannel) return false

        if (title != other.title) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (language != other.language) return false
        if (categories != other.categories) return false
        if (link != other.link) return false
        if (copyright != other.copyright) return false
        if (managingEditor != other.managingEditor) return false
        if (webMaster != other.webMaster) return false
        if (pubDate != other.pubDate) return false
        if (lastBuildDate != other.lastBuildDate) return false
        if (generator != other.generator) return false
        if (docs != other.docs) return false
        if (cloud != other.cloud) return false
        if (ttl != other.ttl) return false
        if (rating != other.rating) return false
        if (textInput != other.textInput) return false
        if (skipHours != other.skipHours) return false
        if (skipDays != other.skipDays) return false
        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + (categories?.hashCode() ?: 0)
        result = 31 * result + (link?.hashCode() ?: 0)
        result = 31 * result + (copyright?.hashCode() ?: 0)
        result = 31 * result + (managingEditor?.hashCode() ?: 0)
        result = 31 * result + (webMaster?.hashCode() ?: 0)
        result = 31 * result + (pubDate?.hashCode() ?: 0)
        result = 31 * result + (lastBuildDate?.hashCode() ?: 0)
        result = 31 * result + (generator?.hashCode() ?: 0)
        result = 31 * result + (docs?.hashCode() ?: 0)
        result = 31 * result + (cloud?.hashCode() ?: 0)
        result = 31 * result + (ttl ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (textInput?.hashCode() ?: 0)
        result = 31 * result + (skipHours?.hashCode() ?: 0)
        result = 31 * result + (skipDays?.hashCode() ?: 0)
        result = 31 * result + items.hashCode()
        return result
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder().apply {
            appendLine("title:$title")
            appendLine("description:$description")
            appendLine("image:$image")
            appendLine("language:$language")
            appendLine("categories:$categories")
            appendLine("link:$link")
            appendLine("copyright:$copyright")
            appendLine("managingEditor:$managingEditor")
            appendLine("webMaster:$webMaster")
            appendLine("pubDate:$pubDate")
            appendLine("lastBuildDate:$lastBuildDate")
            appendLine("generator:$generator")
            appendLine("docs:$docs")
            appendLine("cloud:$cloud")
            appendLine("ttl:$ttl")
            appendLine("rating:$rating")
            appendLine("textInput:$textInput")
            appendLine("skipHours:$skipHours")
            appendLine("skipDays:$skipDays")
            appendLine("items:$items")
        }

        return stringBuilder.toString()
    }
}

data class ITunesChannel(
    override val title: String?,
    override val description: String?,
    override val image: Image?,
    override val language: String?,
    override val categories: List<Category>?,
    override val link: String?,
    override val copyright: String?,
    override val managingEditor: String?,
    override val webMaster: String?,
    override val pubDate: String?,
    override val lastBuildDate: String?,
    override val generator: String?,
    override val docs: String?,
    override val cloud: Cloud?,
    override val ttl: Int?,
    override val rating: String?,
    override val textInput: TextInput?,
    override val skipHours: List<Int>?,
    override val skipDays: List<String>?,
    override val items: List<ITunesItem>?,
    val simpleTitle: String?,
    val explicit: Boolean?,
    val author: String?,
    val owner: Owner?,
    val type: String?,
    val newFeedUrl: String?,
    val block: Boolean?,
    val complete: Boolean?,
) : RssStandardChannel(
    title = title,
    description = description,
    image = image,
    language = language,
    categories = categories,
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
), Serializable

data class GoogleChannel(
    override val title: String?,
    override val description: String?,
    override val image: Image?,
    override val language: String?,
    override val categories: List<Category>?,
    override val link: String?,
    override val copyright: String?,
    override val managingEditor: String?,
    override val webMaster: String?,
    override val pubDate: String?,
    override val lastBuildDate: String?,
    override val generator: String?,
    override val docs: String?,
    override val cloud: Cloud?,
    override val ttl: Int?,
    override val rating: String?,
    override val textInput: TextInput?,
    override val skipHours: List<Int>?,
    override val skipDays: List<String>?,
    override val items: List<GoogleItem>?,
    val explicit: Boolean?,
    val author: String?,
    val owner: String?,
    val block: Boolean?,
    val email: String?,
) : RssStandardChannel(
    title = title,
    description = description,
    image = image,
    language = language,
    categories = categories,
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
), Serializable

data class AutoMixChannel(
    override val title: String?,
    override val description: String?,
    override val image: Image?,
    override val language: String?,
    override val categories: List<Category>?,
    override val link: String?,
    override val copyright: String?,
    override val managingEditor: String?,
    override val webMaster: String?,
    override val pubDate: String?,
    override val lastBuildDate: String?,
    override val generator: String?,
    override val docs: String?,
    override val cloud: Cloud?,
    override val ttl: Int?,
    override val rating: String?,
    override val textInput: TextInput?,
    override val skipHours: List<Int>?,
    override val skipDays: List<String>?,
    override val items: List<AutoMixItem>?,
    val simpleTitle: String?,
    val explicit: Boolean?,
    val email: String?,
    val author: String?,
    val owner: Owner?,
    val type: String?,
    val newFeedUrl: String?,
    val block: Boolean?,
    val complete: Boolean?,
) : RssStandardChannel(
    title = title,
    description = description,
    image = image,
    language = language,
    categories = categories,
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
), Serializable