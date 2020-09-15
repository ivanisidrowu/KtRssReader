package tw.ktrssreader.config

object KtRssReaderGlobalConfig {
    var enableLog: Boolean = false
}

fun readerGlobalConfig(builder: KtRssReaderGlobalConfig.() -> Unit) {
    KtRssReaderGlobalConfig.apply(builder)
}