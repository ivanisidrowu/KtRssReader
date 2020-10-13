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
        val RSS_DATA: RssData  = RssData(
            title = "channel title",
            link = "http://channel.link",
            textInput = MyTextInput(title = "channel textInput title", name = "channel textInput name"),
            list = listOf(
                RssItem(title = "item title - Full", author = "item.author@example.com"),
                RssItem(title = "item title - Partial", author = null),
                RssItem(title = null, author = null)
            ),
            categories = listOf("channel category 1", "channel category 2"),
            skipDays = SkipDays(listOf("Saturday", "Sunday")),
            ttl = 60L,
            image = TestImage(
                link = "http://channel.image.link",
                title = "channel image title",
                height = 32,
                width = 96
            )
        )
    }
}