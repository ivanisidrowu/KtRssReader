package tw.ktrssreader.parser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.model.channel.RssStandardChannel
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

class RssStandardParser : ParserBase<RssStandardChannel>() {
    @Throws(XmlPullParserException::class)
    override fun parse(xml: String): RssStandardChannel {
        return parseStandardChannel(xml)
    }

    override suspend fun parseSuspend(xml: String) = suspendCoroutine<RssStandardChannel> { parse(xml) }

    override fun parseFlow(xml: String): Flow<RssStandardChannel> {
        return flow { emit(parse(xml)) }
    }
}