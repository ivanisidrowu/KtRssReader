### Auto Mix Channel Tags Mapping Table

|[Atuo Mix Channel Tags](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L145)| [RSS 2.0 Standard](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L29) | [iTunes Podcast](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L64) | [Google Play Podcast](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L104) |
| ------ | ------ | ------ | ------ |
|title|title|title|title|
|description|description|description|googleplay:description|
|image|image|itunes:image|googleplay:image|
|language|language|language|language|
|categories|category|itunes:category|googleplay:category|
|link|link|link|link|
|copyright|copyright|copyright|copyright|
|managingEditor|managingEditor|managingEditor|managingEditor|
|webMaster|webMaster|webMaster|webMaster|
|pubDate|pubDate|pubDate|pubDate|
|lastBuildDate|lastBuildDate|lastBuildDate|lastBuildDate|
|generator|generator|generator|generator|
|docs|docs|docs|docs|
|cloud|cloud|cloud|cloud|
|ttl|ttl|ttl|ttl|
|rating| rating|rating|rating|
|textInput|textInput|textInput|textInput|
|skipHours|skipHours|skipHours|skipHours|
|skipDays|skipDays|skipDays|skipDays|
|simpleTitle|-|itunes:title|-|
|explicit|-|itunes:explict|googleplay:explicit|
|email|-|itunes:email|-|
|author|-|itunes:author|googleplay:author|
|owner|-|itunes:owner|googleplay:owner|
|type|-|itunes:type|-|
|newFeedUrl|-|itunes:new-feed-url|-|
|block|-|itunes:block|googleplay:block|
|complete|-|itunes:complete|-|


### Auto Mix Item Tags Mapping Table

|[Atuo Mix Item Tags](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/item/Items.kt#L94)| [RSS 2.0 Standard](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/item/Items.kt#L18) | [iTunes Podcast](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/item/Items.kt#L42) | [Google Play Podcast](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/item/Items.kt#L68)
| ------ | ------ | ------ | ------ |
|title | title | title | title |
|enclosure|enclosure|enclosure|enclosure|
|guid|guid|guid|guid|
|pubDate|pubDate|pubDate|pubDate|
|description|description|description|googleplay:description|
|link|link|link|link|
|author|author|author|author|
|categories|category|category|category|
|comments|comments|comments|comments|
|source|source|source|source|
|simpleTitle|-|itunes:title|-|
|duration|-|itunes:duration|-|
|image|-|itunes:image|-|
|explicit|-|itunes:explicit | googleplay:explicit|
|episode|-|itunes:episode|-|-|
|season|-|itunes:season|-|
|episodeType|-|itunes:episodeType|-|
|block|-|itunes:block|googleplay:block|