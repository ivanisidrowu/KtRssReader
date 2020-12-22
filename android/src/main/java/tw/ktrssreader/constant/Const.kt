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

package tw.ktrssreader.constant

import androidx.annotation.IntDef
import tw.ktrssreader.model.channel.AutoMixChannel
import tw.ktrssreader.model.channel.GoogleChannel
import tw.ktrssreader.model.channel.ITunesChannel
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.utils.convertChannelTo

object Const {

    const val RSS_STANDARD = 1
    const val ITUNES = 2
    const val GOOGLE = 3
    const val AUTO_MIX = 4
    const val CUSTOM = 5

    @Target(AnnotationTarget.TYPE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(RSS_STANDARD, ITUNES, GOOGLE, AUTO_MIX, CUSTOM)
    annotation class ChannelType {

        companion object {

            fun convertChannelToType(channel: Any): Int {
                return when (channel) {
                    is ITunesChannel -> ITUNES
                    is GoogleChannel -> GOOGLE
                    is AutoMixChannel -> AUTO_MIX
                    is RssStandardChannel -> RSS_STANDARD
                    else -> CUSTOM
                }
            }

            inline fun <reified T> convertToChannelType(): Int {
                return convertChannelTo<T, Int>(
                    ifRssStandard = { RSS_STANDARD },
                    ifITunes = { ITUNES },
                    ifGoogle = { GOOGLE },
                    ifAutoMix = { AUTO_MIX },
                ) ?: CUSTOM
            }
        }
    }

}