package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.RssStandardChannel

class RssStandardParser : ParserBase<RssStandardChannel>() {
    override suspend fun parseSuspend(xml: String): RssStandardChannel {
        TODO("Not yet implemented")
    }

    override fun parse(xml: String): RssStandardChannel {
        TODO("Not yet implemented")
    }

    override fun parseFlow(xml: String): Flow<RssStandardChannel> {
        TODO("Not yet implemented")
    }
}