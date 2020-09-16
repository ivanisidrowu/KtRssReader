package tw.ktrssreader.config

import java.nio.charset.Charset
import kotlin.time.ExperimentalTime
import kotlin.time.days

class KtRssReaderConfig {
    var charset: Charset = Charsets.UTF_8

    var useRemote: Boolean = false

    @ExperimentalTime
    var expiredTime: Long = 1.days.toLongMilliseconds()
}