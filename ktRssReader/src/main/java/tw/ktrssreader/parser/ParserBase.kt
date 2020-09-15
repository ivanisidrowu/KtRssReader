package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.RssStandardChannel

abstract class ParserBase<out T : RssStandardChannel> : Parser<T>