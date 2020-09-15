package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.RssStandardChannel

internal interface Parser<out T : RssStandardChannel> {
    suspend fun parseSuspend(xml: String): T
    fun parse(xml: String): T
    fun parseFlow(xml: String): Flow<T>
}