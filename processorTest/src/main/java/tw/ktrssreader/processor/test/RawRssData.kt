/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tw.ktrssreader.processor.test

import java.io.Serializable
import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag

@RssTag(name = "channel")
data class RawRssData(
    @RssRawData(["googleplay:author", "googleplay:owner"])
    val author: String?,
    @RssRawData(["itunes:owner"])
    val owner: RawOwner?,
    @RssTag(name = "item")
    val list: List<RawRssItem>,
) : Serializable

@RssTag(name = "item")
data class RawRssItem(
    @RssRawData(["itunes:author", "title", "googleplay:description"])
    val info: String?,
    val link: String?,
    val enclosure: RawEnclosure?,
    @RssRawData(["itunes:explicit", "googleplay:explicit"])
    val explicit: Boolean?,
)

@RssTag(name = "enclosure")
data class RawEnclosure(
    @RssAttribute
    val length: Long?,
    @RssAttribute
    val url: String?,
)

@RssTag(name = "owner")
data class RawOwner(
    val name: String?,
    val email: String?,
) : Serializable
