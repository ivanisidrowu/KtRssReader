package tw.ktrssreader.model.channel

import java.io.Serializable

data class Image(
    val link: String?,
    val title: String?,
    val url: String?
) : Serializable