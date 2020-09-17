package tw.ktrssreader.model.channel

import java.io.Serializable

data class TextInput(
    val title: String?,
    val description: String?,
    val name: String?,
    val link: String?,
) : Serializable