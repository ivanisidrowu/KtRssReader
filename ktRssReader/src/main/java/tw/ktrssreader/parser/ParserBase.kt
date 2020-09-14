package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.RssStandardChannel

abstract class ParserBase<T : RssStandardChannel> : Parser<T>