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
        val RSS_DATA: TestRssData = TestRssData(
            title = "channel title",
            link = "http://channel.link",
            textInput = MyTextInput(
                title = "channel textInput title",
                name = "channel textInput name"
            ),
            list = listOf(
                RssItem(
                    title = "item title - Full",
                    author = "item.author@example.com",
                    guid = TestGuid(null)
                ),
                RssItem(
                    title = "item title - Partial",
                    author = null,
                    guid = TestGuid(true)
                ),
                RssItem(
                    title = null,
                    author = null,
                    guid = null
                )
            ),
            categories = listOf(
                TestCategory(
                    domain = null,
                    categoryValue = "channel category 1"
                ),
                TestCategory(
                    domain = "http://channel.category.domain",
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
            cloud = TestCloud(
                domain = "channel.cloud.domain",
                testPort = 80,
                path = "/RPC2"
            )
        )

        val MIX_RSS_DATA_1: MixRssData = MixRssData(
            title = "channel title",
            link = "http://channel.link",
            list = listOf(
                MixRssItem(
                    title = "item title - iTunes",
                    author = "item author - iTunes",
                    explicit = true,
                    block = false,
                    description = "item description <a href=\"http://item.description\">item description</a>.",
                    image = MixImage(
                        link = null,
                        title = null,
                        height = null,
                        width = null,
                        href = "https://item.image.itunes"
                    )
                ),
                MixRssItem(
                    title = "item title - iTunes",
                    author = "item author - iTunes",
                    explicit = true,
                    block = true,
                    description = "item description - google play",
                    image = MixImage(
                        link = null,
                        title = null,
                        height = null,
                        width = null,
                        href = "https://item.image.itunes"
                    )
                ),
                MixRssItem(
                    title = "item title - Partial",
                    author = "item.author@example.com",
                    explicit = true,
                    block = true,
                    description = "item description - google play",
                    image = null
                ),
            ),
            categories = MixCategory(
                categories = listOf(),
                text = "Technology - iTunes"
            ),
            description = "channel description - google play",
            image = MixImage(
                link = null,
                title = null,
                height = null,
                width = null,
                href = "https://channel.image.itunes"
            )
        )

        val MIX_RSS_DATA_2: MixRssData = MixRssData(
            title = "channel title",
            link = "http://channel.link",
            list = listOf(
                MixRssItem(
                    title = "item title - iTunes",
                    author = "item author - iTunes",
                    explicit = true,
                    block = false,
                    description = "item description <a href=\"http://item.description\">item description</a>.",
                    image = MixImage(
                        link = null,
                        title = null,
                        height = null,
                        width = null,
                        href = "https://item.image.itunes"
                    )
                ),
                MixRssItem(
                    title = "item title - iTunes",
                    author = "item author - iTunes",
                    explicit = true,
                    block = true,
                    description = "item description - google play",
                    image = MixImage(
                        link = null,
                        title = null,
                        height = null,
                        width = null,
                        href = "https://item.image.itunes"
                    )
                ),
                MixRssItem(
                    title = "item title - Partial",
                    author = "item.author@example.com",
                    explicit = true,
                    block = true,
                    description = "item description - google play",
                    image = null
                ),
            ),
            categories = MixCategory(
                categories = listOf(
                    MixCategory(
                        categories = listOf(),
                        text = "Technology - iTunes"
                    )
                ),
                text = "Technology - iTunes"
            ),
            description = "channel description - google play",
            image = MixImage(
                link = null,
                title = null,
                height = null,
                width = null,
                href = "https://channel.image.itunes"
            )
        )


        val MIX_RSS_DATA_3: MixRssData = MixRssData(
            title = "channel title",
            link = "http://channel.link",
            list = listOf(),
            categories = MixCategory(
                categories = listOf(),
                text = "Technology - iTunes"
            ),
            description = "channel description - google play",
            image = MixImage(
                link = null,
                title = null,
                height = null,
                width = null,
                href = "https://channel.image.itunes"
            )
        )
        val MIX_RSS_DATA_4: MixRssData = MixRssData(
            title = "channel title",
            link = "http://channel.link",
            list = listOf(),
            categories = MixCategory(
                categories = listOf(),
                text = "Games & Hobbies"
            ),
            description = "channel description - google play",
            image = MixImage(
                link = "http://channel.image.link",
                title = "channel image title",
                height = 32,
                width = 96,
                href = null
            )
        )
        val RAW_RSS_DATA_1: RawRssData = RawRssData(
            author = "channel author - google play",
            owner = RawOwner(
                name = "channel owner name - iTunes",
                email = "channel.owner.email.itunes@example.com"
            ),
            list = listOf(
                RawRssItem(
                    info = "item author - iTunes",
                    link = "http://item.link",
                    enclosure = RawEnclosure(
                        length = 24986239,
                        url = "http://item.enclosure.url/item.mp3"
                    ),
                    explicit = true
                ),
                RawRssItem(
                    info = "item author - iTunes",
                    link = "http://item.link",
                    enclosure = RawEnclosure(
                        length = 24986239,
                        url = "http://item.enclosure.url/item.mp3"
                    ),
                    explicit = true
                ),
                RawRssItem(
                    info = "item title - Partial",
                    link = "http://item.link",
                    enclosure = RawEnclosure(
                        length = 24986239,
                        url = "http://item.enclosure.url/item.mp3"
                    ),
                    explicit = true
                ),
            )
        )
        val RAW_RSS_DATA_2: RawRssData = RawRssData(
            author = "channel author - google play",
            owner = RawOwner(
                name = "channel owner name - iTunes",
                email = "channel.owner.email.itunes@example.com"
            ),
            list = listOf()
        )
        val RAW_RSS_DATA_3: RawRssData = RawRssData(
            author = "channel author - google play",
            owner = null,
            list = listOf()
        )
    }
}