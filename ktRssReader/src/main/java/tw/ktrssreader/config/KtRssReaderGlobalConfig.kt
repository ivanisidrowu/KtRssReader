package tw.ktrssreader.config

import android.content.Context

object KtRssReaderGlobalConfig {
    lateinit var applicationContext: Context
    var enableLog: Boolean = false
}

fun readerGlobalConfig(builder: KtRssReaderGlobalConfig.() -> Unit) {
    KtRssReaderGlobalConfig.apply(builder)
}