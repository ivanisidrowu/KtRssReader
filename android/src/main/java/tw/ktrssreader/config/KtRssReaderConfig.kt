/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tw.ktrssreader.config

import java.nio.charset.Charset
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class KtRssReaderConfig {
    var charset: Charset? = null

    var useCache: Boolean = true

    var flushCache: Boolean = false

    @ExperimentalTime
    var expiredTimeMillis: Long = 1.days.inWholeMilliseconds
}
