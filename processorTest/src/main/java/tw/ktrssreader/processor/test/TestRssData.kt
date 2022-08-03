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

import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssTag
import tw.ktrssreader.annotation.RssValue
import java.io.Serializable

@RssTag(name = "channel")
data class TestRssData(
    val title: String?,
    @RssTag
    val link: String?,
    val textInput: MyTextInput?,
    @RssTag(name = "item")
    val list: List<RssItem>,
    @RssTag(name = "category")
    val categories: List<TestCategory>,
    val skipDays: SkipDays?,
    val ttl: Long?,
    val image: TestImage?,
    val cloud: TestCloud?,
) : Serializable

@RssTag(name = "textInput")
data class MyTextInput(
    val title: String?,
    val name: String?
) : Serializable

@RssTag(name = "item")
data class RssItem(
    val title: String?,
    val author: String?,
    val guid: TestGuid?
) : Serializable

@RssTag(name = "image")
data class TestImage(
    val link: String?,
    val title: String?,
    val height: Int?,
    val width: Int?
) : Serializable

@RssTag(name = "skipDays")
data class SkipDays(
    @RssTag(name = "day")
    val days: List<String>,
) : Serializable

@RssTag(name = "cloud")
data class TestCloud(
    @RssAttribute
    val domain: String?,
    @RssAttribute(name = "port")
    val testPort: Int?,
    @RssAttribute(name = "path")
    val path: String?
) : Serializable

@RssTag(name = "guid")
data class TestGuid(
    @RssAttribute
    val isPermaLink: Boolean?
) : Serializable

@RssTag(name = "category")
data class TestCategory(
    @RssAttribute
    val domain: String?,
    @RssValue
    val categoryValue: String?
) : Serializable
