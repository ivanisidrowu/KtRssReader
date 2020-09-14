package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.RssStandardChannel

interface Parser<T : RssStandardChannel> {
    suspend fun parseSuspend(xml: String): T
    fun parse(): T
    fun parseFlow(): Flow<T>
}