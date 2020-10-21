# KtRssReader


[![](https://jitpack.io/v/ivanisidrowu/KtRssReader.svg)](https://jitpack.io/#ivanisidrowu/KtRssReader)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Android Weekly](https://img.shields.io/badge/Android%20Weekly-%23435-red.svg)](http://androidweekly.net/issues/issue-435)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-KtRssReader-yellow.svg?style=flat)](https://android-arsenal.com/details/1/8178)

KtRssReader is a Kotlin library for parsing RSS feed on Android.

* Supports RSS 2.0 standard, iTunes, and Google Play tags
* Easy-to-use API
* Fetches feed for you
* Database cache and custom cache valid time
* Customizes output data with annotations

## Download

First, you can add this repository to the root of your project `build.gradle` file under the **`allprojects`**.

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
    implementation "com.github.ivanisidrowu.KtRssReader:ktRssReader:v2.0.0"
}
```

If you want to customize data format, you have to add these dependencies.

```gradle
apply plugin: 'kotlin-kapt'

dependencies {
    implementation "com.github.ivanisidrowu.KtRssReader:ktRssReader:v2.0.0"
    implementation "com.github.ivanisidrowu.KtRssReader:annotation:v2.0.0"
    kapt "com.github.ivanisidrowu.KtRssReader:processor:v2.0.0"
}
```

## Data Models

Before we get into the basic API usage, let's talk about the data models in KtRssReader. KtRssReader provides 4 model classes and custom annotations for you to get different kinds of tags you need.

### RSS 2.0 Standard Format

[`RssStandardChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L29) / [`RssStandardChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L6): To get tags and values from [RSS 2.0 standard](https://cyber.harvard.edu/rss/rss.html).

### iTunes Podcast Format

[`ITunesChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L64) / [`ITunesChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L52): To get [iTunes](https://help.apple.com/itc/podcasts_connect/#/itcb54353390) tags from a source.

### Google Play Podcast Format

[`GoogleChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L104) / [`GoogleChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L52): To get [Google Play](https://support.google.com/podcast-publishers/answer/9889544?hl=en) tags.

### Auto Mix Data

[`AutoMixChannelData`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L145) / [`AutoMixChannel`](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt#L132): It automatically merges tags by the following order: `RSS 2.0 -> iTunes -> Google Play`.

The first tags it will look into are RSS 2.0 standard tags, if RSS 2.0 doesn't have usable values, the parser will look for iTunes tags as an alternative source. Then, if even iTunes tags don't have usable values, the parser will eventually look for Google Play tags as the second alternative source. For example, we got `<image>` tag in the RSS source. We would like to find usable values of the image. So the parser will look for values by the order of `<image>`, `<itunes:image>`, and `<googleplay:image>`. For merging tags, `AutoMixChannelData` will automatically merge data by their tag names without the platform prefixes. View the tag mapping table [here](TAGS_MAPPING_TABLE.md).

In short, `AutoMixChannelData` and `AutoMixChannel` can provide you the union set of all tags and values from all formats.

### Custom Data

In KtRssReader, we provide annotations to let you define custom models.
- Don't forget to **rebuild** the project after defining custom data classes with annotations!
- Remember, **every custom data class is required to implement `Serializable`.**

#### `@RssTag`

Params:
- `name`: The tag name in `String`.
- `order`: Custom parsing order array. The type in the array is `OrderType` enum. 3 types are available, `RSS_STANDARD`, `ITUNES`, `GOOGLE`. The default order array is `[OrderType.RSS_STANDARD, OrderType.ITUNES, OrderType.GOOGLE]`. When parsing the RSS feed, the parser will follow the order to find the available value for the property. Take the default order array as an example, the parser will check the RSS standard tag value first, if it has a value, it will not check the iTunes tag and put the RSS standard tag value to the custom model. Otherwise, it will continue to find an available value.

Class example:
```kotlin
@RssTag(name = "channel")
data class MyChannel(
    val title: String?,
    @RssTag(name = "author", order = [OrderType.ITUNES, OrderType.RSS_STANDARD])
    val name: String?,
): Serializable
```
Data example:
```xml
<channel>
    <title>the title</title>
    <author>the author</author>
    <itunes:author>itunes author</itunes:author>
</channel>
```

#### `@RssAttribute`

Params:
- `name`: The name of the attribute.

Class example:
```kotlin
@RssTag(name = "category")
data class Category(
    @RssAttribute(name = "title")
    val categoryTitle: String?,
): Serializable
```

Data example:
```xml
<category title = "the title">this is a category</category>
```

The value of the `categoryTitle` will be `"the title"`.

#### `@RssValue`

Get the value inside a tag.

Class example:
```kotlin
@RssTag(name = "category")
data class Category(
    @RssValue
    val value: String?,
    @RssAttribute
    val title: String?,
): Serializable
```

Data example:
```xml
<category title = "the title">this is a category</category>
```

In this case, the property `value` of the data class will be `"this is a category"`.

#### `@RssRawData`

An annotation to let you parse tags with specific raw data in RSS feed.

Params:
- rawTags: It's an array that contains tag names you would like to parse, and the parser will follow the order of the array to find tag candidates.

Class example:
```kotlin
@RssTag(name = "channel")
data class RawRssData(
    @RssRawData(rawTags = ["googleplay:author", "itunes:author"])
    val author: String?,
    val title: String?,
): Serializable
```

Data example:
```xml
<channel>
    <itunes:author>itunes author</itunes:author>
    <titile>title</title>
</channel>
```

The parsing result of the `author`, in this case, will be `"itunes author"` because the tag `<googleplay:author>` does not exist in the data. So the parser uses the backup tag `<itunes:author>` defined in `@RssRawData`.

#### Reader Code Generation

After you define your custom data, KtRssReader will generate reader code for you. For instance, if I defined a data class named `PodcastChannel` which has the data of the `<channel>` tag, the KtRssReader will automatically generate `PodcastChannelReader` after rebuilding the project.

## How to Use KtRssReader?

### Basic Usage

```kotlin
val result: RssStandardChannelData = Reader.read<RssStandardChannelData>(rssSource)
```

This is the simplest way to use it. As you can see, `Reader.read()` takes a generic type called `RssStandardChannelData`. You can also use alternatives such as `ITunesChannelData` or `AutoMixChannelData` depends on you what you need. Alternative classes can found in [Channels.kt](https://github.com/ivanisidrowu/KtRssReader/blob/master/ktRssReader/src/main/java/tw/ktrssreader/model/channel/Channels.kt). The reader method should not be executed in the main thread or it will throw an exception to warn lib users.

### With Flow

```kotlin
Reader.flowRead<AutoMixChannelData>(rssSource)
    .flowOn(Dispatchers.IO)
    .collect { data ->
        Log.d("KtRssReader", data)
    }
```

### With Coroutines

```kotlin
coroutineScope.launch(Dispatchers.IO) {
    val result = Reader.coRead<GoogleChannelData>(rssSource)
    Log.d("KtRssReader", result)
}
```

### With Custom Data

1. Define your custom data classes.

```kotlin
@RssTag(name = "channel")
data class PodcastChannel(
    val title: String?,
    @RssTag(name = "author", order = [OrderType.ITUNES, OrderType.RSS_STANDARD])
    val podcastAuthor: String?,
): Serializable
```

2. Rebuild the project.

3. `PodcastChannelReader` will be generated automatically.
   
4. Use `PodcastChannelReader` as we mentioned in Basic Usage.

```kotlin
val result: PodcastChannel = PodcastChannelReader.read(rssSource)

// Flow
PodcastChannelReader.flowRead(rssSource)
    .flowOn(Dispatchers.IO)
    .collect { data ->
        Log.d("KtRssReader", data)
    }

// Coroutines
val coResult: PodcastChannel = PodcastChannelReader.coRead(rss)
```

### Clear Cache

Clear all cache in the database.

```kotlin
Reader.clearCache()
```

## Config

### Global Config

To let KtRssReader works with the database, you need to set the application context in your application.

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        readerGlobalConfig {
            setApplicationContext(this@MyApplication)
            enableLog = true
        }
    }
}
```

* `setApplicationContext()`: The application context.
* `enableLog`: If this is enabled, the debug log will be shown on the console.

### Reader Config

```kotlin
val result: ITunesChannelData = Reader.read<ITunesChannelData>(rssSource) {
    charset = Charsets.UTF_8
    useCache = false
    expiredTimeMillis = 600000L
}
```

* `charset`: Specify an encoding charset, if it's not set, it will use the charset from the RSS source.
* `useCache`: Pull data from cache or remote server. The default setting is set to true.
* `flushCache`: Clear the specific cache by URL.
* `expiredTimeMillis`: The cache expired time in milliseconds.

## Samples

The sample App is in `/app` folder. [Check it out!](https://github.com/ivanisidrowu/KtRssReader/tree/master/app)

## Contribution

Contributions are always welcome. If you have any ideas or suggestions, you can contact us or create a Github issue. We will get to you as soon as possible.

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