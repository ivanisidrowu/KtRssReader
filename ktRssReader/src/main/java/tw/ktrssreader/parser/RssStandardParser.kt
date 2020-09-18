package tw.ktrssreader.parser

import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.model.channel.RssStandardChannel

class RssStandardParser : ParserBase<RssStandardChannel>() {
    @Throws(XmlPullParserException::class)
    override fun parse(xml: String): RssStandardChannel {
        return parseStandardChannel(xml)
    }
}