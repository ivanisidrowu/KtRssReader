package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.ITunesChannel

class ITunesParser : ParserBase<ITunesChannel>() {
    override suspend fun parseSuspend(xml: String): ITunesChannel {
        TODO("Not yet implemented")
    }

    override fun parse(): ITunesChannel {
        TODO("Not yet implemented")
    }

    override fun parseFlow(): Flow<ITunesChannel> {
        TODO("Not yet implemented")
    }
}