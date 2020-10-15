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

package tw.ktrssreader.processor.test.data

import tw.ktrssreader.processor.test.*

class TestData {
    companion object {
        val RSS_DATA: TestRssData  = TestRssData(
            title = "channel title",
            link = "http://channel.link",
            textInput = MyTextInput(title = "channel textInput title", name = "channel textInput name"),
            list = listOf(
                RssItem(title = "item title - Full", author = "item.author@example.com", guid = TestGuid(null)),
                RssItem(title = "item title - Partial", author = null, guid = TestGuid(true)),
                RssItem(title = null, author = null, guid = null)
            ),
            categories = listOf(
                TestCategory(domain = null,
                    categoryValue = "channel category 1"
                ),
                TestCategory(domain = "http://channel.category.domain",
                    categoryValue = "channel category 2"
                ),
            ),
            skipDays = SkipDays(listOf("Saturday", "Sunday")),
            ttl = 60L,
            image = TestImage(
                link = "http://channel.image.link",
                title = "channel image title",
                height = 32,
                width = 96
            ),
            cloud = TestCloud(domain = "channel.cloud.domain", testPort = 80, path = "/RPC2")
        )
    }
}