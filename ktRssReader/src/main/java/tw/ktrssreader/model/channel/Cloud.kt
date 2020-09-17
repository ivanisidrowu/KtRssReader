package tw.ktrssreader.model.channel

import java.io.Serializable

data class Cloud(
    val domain: String?,
    val port: Int?,
    val path: String?,
    val registerProcedure: String?,
    val protocol: String?,
) : Serializable