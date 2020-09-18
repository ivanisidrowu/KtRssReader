package tw.ktrssreader.config

import android.content.Context

object KtRssReaderGlobalConfig {
    private var applicationContext: Context? = null
    var enableLog: Boolean = false

    fun getApplicationContext(): Context {
        return applicationContext
            ?: error("No Application Context configured. Please use readerGlobalConfig() DSL.")
    }
}

fun readerGlobalConfig(builder: KtRssReaderGlobalConfig.() -> Unit) {
    return KtRssReaderGlobalConfig.run(builder)
}