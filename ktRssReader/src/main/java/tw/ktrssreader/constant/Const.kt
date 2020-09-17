package tw.ktrssreader.constant

import androidx.annotation.IntDef

object Const {

    const val RSS_STANDARD = 1
    const val ITUNES = 2
    const val GOOGLE = 3
    const val AUTO_MIX = 4

    @Target(AnnotationTarget.TYPE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(RSS_STANDARD, ITUNES, GOOGLE, AUTO_MIX)
    annotation class ChannelType

}