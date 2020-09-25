# KtRssReader

KtRssReader is a Kotlin library for parsing RSS feed on Android.

* Supports RSS 2.0 standard, iTunes, and Google Play tags
* Easy-to-use API
* Fetches feed for you
* Database cache and custom cache valid time

## Getting Started

### Download

First, you can add this repository to the root of your project `build.gradle` file.

```gradle
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}
```

Then, add this dependency to the `build.gradle` file in app directory.

```gradle
dependencies {
  implemetation "com.github.ivanisidrowu:KtRssReader:${lib_version}"
}
```

### Data Models

Before we get into the basic API usage, let talk about the data models in KtRssReader. KtRssReader provides 4 model classes for you to get different kinds of tags you need.

#### RSS 2.0 Standard Format

[`RssStandardChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L29) / [`RssStandardChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L6): To get tags and values from [RSS 2.0 standard](https://cyber.harvard.edu/rss/rss.html).

#### iTunes Podcast Format

[`ITunesChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L64) / [`ITunesChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L52): To get [iTunes](https://help.apple.com/itc/podcasts_connect/#/itcb54353390) tags from a source.

#### Google Play Podcast Format

[`GoogleChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L104) / [`GoogleChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L52): To get [Google Play](https://support.google.com/podcast-publishers/answer/9889544?hl=en) tags.

#### Auto Mix Data

[`AutoMixChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L145) / [`AutoMixChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L132): It automatically merges tags by the following order: `RSS 2.0 -> iTunes -> Google Play`.

The first tags it will look into are RSS 2.0 standard tags, if RSS 2.0 doesn't have usable values, the parser will look for iTunes tags as an alternative source. Then, if even iTunes tags don't have usable values, the parser will eventually look for Google Play tags as the second alternative source. For example, we got `<image>` tag in the RSS source. We would like to find usable values of the image. So the parser will look for values by the order of `<image>`, `<itunes:image>`, and `<googleplay:image>`. For merging tags, `AutoMixChannelData` will automatically merge data by their tag names without the platform prefixes.

In short, `AutoMixChannelData` and `AutoMixChannel` can provide you the union set of all tags and values from all formats.

### How to Use KtRssReader?

#### Basic Usage

```kotlin
val result: RssStandardChannelData = Reader.read<RssStandardChannelData>(rssSource)
```

This is the simplest way to use it. As you can see, `Reader.read()` takes a generic type called `RssStandardChannelData`. You can also use alternatives such as `ITunesChannelData` or `AutoMixChannelData` depends on you what you need. Alternative classes can found in [Channels.kt](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt). The reader method should not be executed in the main thread or it will throw an exception to warn lib users.

#### Config

* Global Config

To let KtRssReader works with the database, you need to set the application context in your application.

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        readerGlobalConfig {
            applicationContext = this@MyApplication
            enableLog = true
        }
    }
}
```

* `applicationContext`: The application context.
* `enableLog`: If this is enabled, the debug log will be shown on the console.

* Reader Config

```kotlin
val result: ITunesChannelData = Reader.read<ITunesChannelData>(rssSource) {
    charset = Charsets.UTF_8
    useRemote = true
    expiredTimeMillis = 600000
}
```

* `charset`: Specify an encoding charset, if it's not set, it will use the charset from the RSS source.
* `useRemote`: Pull data from cache or remote server.
* `expiredTimeMillis`: The cache expire time in milliseconds.

#### With Flow

```kotlin
Reader.flow<AutoMixChannelData>(rssSource)
    .flowOn(Dispatchers.IO)
    .collect { data ->
        Log.d("KtRssReader", data)
    }
```

#### With Coroutines

```kotlin
coroutineScope.launch(Dispatchers.IO) {
    val result = Reader.suspend<GoogleChannelData>(rssSource)
    Log.d("KtRssReader", result)
}
```

## Samples

Working in progress.

## Contribution
Contributions are always welcome. If you have any ideas or suggestions, you can contact us or create a Github issue. We will get to you as soon as possible.

## Roadmap

* We are planning to provide annotations to let users customize their data class with specified names and tags. This feature will give lib users total control of their parsing results.
* We also want to implement an annotation processor to process annotation and generate parsers in compile time.
* Allow users to parse data with raw tag names.

## License

```
Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```