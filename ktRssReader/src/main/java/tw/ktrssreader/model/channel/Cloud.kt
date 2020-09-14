package tw.ktrssreader.model.channel

data class Cloud(
    val domain: String?,
    val port: Int?,
    val path: String?,
    val registerProcedure: String?,
    val protocol: String,
)