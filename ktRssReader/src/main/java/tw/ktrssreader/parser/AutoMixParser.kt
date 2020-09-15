package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import tw.ktrssreader.model.channel.AutoMixChannel

class AutoMixParser : ParserBase<AutoMixChannel>() {

    override suspend fun parseSuspend(xml: String): AutoMixChannel {
        TODO("Not yet implemented")
    }

    override fun parse(xml: String): AutoMixChannel {
        TODO("Not yet implemented")
    }

    override fun parseFlow(xml: String): Flow<AutoMixChannel> {
        TODO("Not yet implemented")
    }
}