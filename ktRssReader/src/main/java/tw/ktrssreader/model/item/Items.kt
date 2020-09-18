package tw.ktrssreader.model.item

import java.io.Serializable
import java.lang.StringBuilder

open class RssStandardItem(
    open val title: String?,
    open val enclosure: Enclosure?,
    open val guid: Guid?,
    open val pubDate: String?,
    open val description: String?,
    open val link: String?,
    open val author: String?,
    open val categories: List<Category>?,
    open val comments: String?,
    open val source: Source?,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RssStandardItem) return false

        if (title != other.title) return false
        if (enclosure != other.enclosure) return false
        if (guid != other.guid) return false
        if (pubDate != other.pubDate) return false
        if (description != other.description) return false
        if (link != other.link) return false
        if (author != other.author) return false
        if (categories != other.categories) return false
        if (comments != other.comments) return false
        if (source != other.source) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (enclosure?.hashCode() ?: 0)
        result = 31 * result + (guid?.hashCode() ?: 0)
        result = 31 * result + (pubDate?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (link?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (categories?.hashCode() ?: 0)
        result = 31 * result + (comments?.hashCode() ?: 0)
        result = 31 * result + (source?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder().apply {
            appendLine("title:$title")
            appendLine("enclosure:$enclosure")
            appendLine("guid:$guid")
            appendLine("pubDate:$pubDate")
            appendLine("description:$description")
            appendLine("link:$link")
            appendLine("author:$author")
            appendLine("categories:$categories")
            appendLine("comments:$comments")
            appendLine("source:$source")
        }

        return stringBuilder.toString()
    }
}

data class ITunesItem(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>,
    override val comments: String?,
    override val source: Source?,
    val duration: Long?,
    val image: String?,
    val explicit: Boolean?,
    val episode: Int?,
    val season: Int?,
    val episodeType: String?,
    val block: Boolean?,
) : RssStandardItem(
    title = title,
    enclosure = enclosure,
    guid = guid,
    pubDate = pubDate,
    description = description,
    link = link,
    author = author,
    categories = categories,
    comments = comments,
    source = source,
), Serializable

data class GoogleItem(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>,
    override val comments: String?,
    override val source: Source?,
    val explicit: Boolean?,
    val block: Boolean?,
) : RssStandardItem(
    title = title,
    enclosure = enclosure,
    guid = guid,
    pubDate = pubDate,
    description = description,
    link = link,
    author = author,
    categories = categories,
    comments = comments,
    source = source,
), Serializable

data class AutoMixItem(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>,
    override val comments: String?,
    override val source: Source?,
    val duration: Long?,
    val image: String?,
    val explicit: Boolean?,
    val episode: Int?,
    val season: Int?,
    val episodeType: String?,
    val block: Boolean?,
) : RssStandardItem(
    title = title,
    enclosure = enclosure,
    guid = guid,
    pubDate = pubDate,
    description = description,
    link = link,
    author = author,
    categories = categories,
    comments = comments,
    source = source,
), Serializable