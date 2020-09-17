package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.RssStandardChannel

interface Parser<out T : RssStandardChannel> {
    fun parse(xml: String): T
}