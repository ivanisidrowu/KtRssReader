package tw.ktrssreader.parser

import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.model.channel.RssStandardChannel
import kotlin.jvm.Throws

class RssStandardParser : ParserBase<RssStandardChannel>() {

    override val logTag: String = RssStandardParser::class.java.simpleName

    @Throws(XmlPullParserException::class)
    override fun parse(xml: String): RssStandardChannel {
        return parseStandardChannel(xml)
    }
}