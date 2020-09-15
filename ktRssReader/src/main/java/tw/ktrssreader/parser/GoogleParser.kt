package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.GoogleChannel

internal class GoogleParser : ParserBase<GoogleChannel>() {
    override suspend fun parseSuspend(xml: String): GoogleChannel {
        TODO("Not yet implemented")
    }

    override fun parse(xml: String): GoogleChannel {
        TODO("Not yet implemented")
    }

    override fun parseFlow(xml: String): Flow<GoogleChannel> {
        TODO("Not yet implemented")
    }
}