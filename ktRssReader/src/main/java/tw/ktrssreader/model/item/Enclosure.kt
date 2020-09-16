package tw.ktrssreader.model.item

import java.io.Serializable

data class Enclosure(
    val url: String?,
    val length: Long?,
    val type: String?,
) : Serializable