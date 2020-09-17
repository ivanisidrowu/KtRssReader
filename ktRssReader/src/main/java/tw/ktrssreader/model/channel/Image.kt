package tw.ktrssreader.model.channel

import java.io.Serializable

data class Image(
    val link: String?,
    val title: String?,
    val url: String?,
    val description: String?,
    val height: Int?,
    val width: Int?,
) : Serializable