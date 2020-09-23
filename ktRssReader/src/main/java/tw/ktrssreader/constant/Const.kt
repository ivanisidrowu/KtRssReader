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

    @Target(AnnotationTarget.TYPE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(RSS_STANDARD, ITUNES, GOOGLE, AUTO_MIX)
    annotation class ChannelType {

        companion object {

            fun convertChannelToType(channel: RssStandardChannel): Int {
                return when (channel) {
                    is ITunesChannel -> ITUNES
                    is GoogleChannel -> GOOGLE
                    is AutoMixChannel -> AUTO_MIX
                    else -> RSS_STANDARD
                }
            }

            inline fun <reified T : RssStandardChannel> convertToChannelType(): Int {
                return convertChannelTo<T, Int>(
                    ifRssStandard = { RSS_STANDARD },
                    ifITunes = { ITUNES },
                    ifGoogle = { GOOGLE },
                    ifAutoMix = { AUTO_MIX }
                )
            }
        }
    }

}