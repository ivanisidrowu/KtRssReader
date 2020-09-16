package tw.ktrssreader.model.channel

import java.io.Serializable

data class Owner(
    val name: String?,
    val email: String?,
) : Serializable