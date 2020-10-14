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

import tw.ktrssreader.annotation.OrderType
import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssTag
import java.io.Serializable

@RssTag(name = "channel")
data class RssMixData(
    val title: String?,
    @RssTag
    val link: String?,
    val textInput: MixTextInput?,
    @RssTag(name = "item")
    val list: List<RssMixItem>,
    @RssTag(name = "category", order = [OrderType.ITUNES, OrderType.GOOGLE, OrderType.RSS_STANDARD])
    val categories: List<String>,
    val ttl: Long?,
    val image: MixImage?,
    val cloud: MixCloud?,
): Serializable

@RssTag(name = "textInput")
data class MixTextInput(
    val title: String?,
    val name: String?
): Serializable

@RssTag(name = "item")
data class RssMixItem(
    @RssTag(order = [OrderType.ITUNES, OrderType.RSS_STANDARD, OrderType.GOOGLE])
    val title: String?,
    @RssTag(order = [OrderType.GOOGLE, OrderType.ITUNES, OrderType.RSS_STANDARD])
    val author: String?,
): Serializable

@RssTag(name = "image")
data class MixImage(
    val link: String?,
    val title: String?,
    val height: Int?,
    val width: Int?
)

@RssTag(name = "cloud")
data class MixCloud(
    @RssAttribute
    val domain: String?,
    @RssAttribute(name = "testPort")
    val testPort: Int?,
    @RssAttribute(name = "path")
    val path: String?
)