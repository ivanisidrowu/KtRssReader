package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.ITunesChannel

internal class ITunesParser : ParserBase<ITunesChannel>() {
    override suspend fun parseSuspend(xml: String): ITunesChannel {
        TODO("Not yet implemented")
    }

    override fun parse(xml: String): ITunesChannel {
        TODO("Not yet implemented")
    }

    override fun parseFlow(xml: String): Flow<ITunesChannel> {
        TODO("Not yet implemented")
    }
}