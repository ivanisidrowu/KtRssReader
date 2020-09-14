package tw.ktrssreader.parser

import tw.ktrssreader.model.channel.RssStandardChannel

internal abstract class ParserBase<T : RssStandardChannel> : Parser<T>