package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.GoogleChannel

class GoogleParser : ParserBase<GoogleChannel>() {
    override suspend fun parseSuspend(xml: String): GoogleChannel {
        TODO("Not yet implemented")
    }

    override fun parse(): GoogleChannel {
        TODO("Not yet implemented")
    }

    override fun parseFlow(): Flow<GoogleChannel> {
        TODO("Not yet implemented")
    }
}