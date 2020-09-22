package tw.ktrssreader.model.channel

import tw.ktrssreader.model.item.*
import java.io.Serializable

interface RssStandardChannel : Serializable {
    val title: String?
    val description: String?
    val image: Image?
    val language: String?
    val categories: List<Category>?
    val link: String?
    val copyright: String?
    val managingEditor: String?
    val webMaster: String?
    val pubDate: String?
    val lastBuildDate: String?
    val generator: String?
    val docs: String?
    val cloud: Cloud?
    val ttl: Int?
    val rating: String?
    val textInput: TextInput?
    val skipHours: List<Int>?
    val skipDays: List<String>?
    val items: List<RssStandardItem>?
}

data class RssStandardChannelData(
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
    override val items: List<RssStandardItemData>?,
) : RssStandardChannel

interface ITunesChannel : RssStandardChannel {
    val simpleTitle: String?
    val explicit: Boolean?
    val author: String?
    val owner: Owner?
    val type: String?
    val newFeedUrl: String?
    val block: Boolean?
    val complete: Boolean?
    override val items: List<ITunesItem>?
}

data class ITunesChannelData(
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
    override val items: List<ITunesItemData>?,
    override val simpleTitle: String?,
    override val explicit: Boolean?,
    override val author: String?,
    override val owner: Owner?,
    override val type: String?,
    override val newFeedUrl: String?,
    override val block: Boolean?,
    override val complete: Boolean?,
) : ITunesChannel

interface GoogleChannel : RssStandardChannel {
    val explicit: Boolean?
    val author: String?
    val owner: Owner?
    val block: Boolean?
    val email: String?
    override val items: List<GoogleItem>?
}

data class GoogleChannelData(
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
    override val items: List<GoogleItemData>?,
    override val explicit: Boolean?,
    override val author: String?,
    override val owner: Owner?,
    override val block: Boolean?,
    override val email: String?,
) : GoogleChannel

interface AutoMixChannel : ITunesChannel, GoogleChannel {
    override val simpleTitle: String?
    override val explicit: Boolean?
    override val email: String?
    override val author: String?
    override val owner: Owner?
    override val type: String?
    override val newFeedUrl: String?
    override val block: Boolean?
    override val complete: Boolean?
    override val items: List<AutoMixItem>?
}

data class AutoMixChannelData(
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
    override val simpleTitle: String?,
    override val explicit: Boolean?,
    override val email: String?,
    override val author: String?,
    override val owner: Owner?,
    override val type: String?,
    override val newFeedUrl: String?,
    override val block: Boolean?,
    override val complete: Boolean?,
) : AutoMixChannel