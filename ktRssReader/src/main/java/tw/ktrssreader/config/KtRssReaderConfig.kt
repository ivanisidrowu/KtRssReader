package tw.ktrssreader.config

import java.nio.charset.Charset
import kotlin.time.ExperimentalTime
import kotlin.time.days

class KtRssReaderConfig {
    var charset: Charset? = null

    var useRemote: Boolean = false

    @ExperimentalTime
    var expiredTimeMillis: Long = 1.days.toLongMilliseconds()
}