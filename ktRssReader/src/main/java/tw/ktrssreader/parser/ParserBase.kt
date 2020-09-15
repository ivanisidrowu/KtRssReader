package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.RssStandardChannel

internal abstract class ParserBase<out T : RssStandardChannel> : Parser<T>