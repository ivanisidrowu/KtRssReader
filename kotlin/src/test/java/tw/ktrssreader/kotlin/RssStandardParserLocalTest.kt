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

package tw.ktrssreader.kotlin

import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import tw.ktrssreader.kotlin.model.channel.RssStandardChannelData
import tw.ktrssreader.kotlin.parser.RssStandardParser
import tw.ktrssreader.test.common.ChannelItemTestData.FULL_RSS_CHANNEL
import tw.ktrssreader.test.common.ChannelItemTestData.PARTIAL_RSS_CHANNEL
import tw.ktrssreader.test.common.ChannelItemTestData.RSS_CHANNEL_PARTIAL_CLOUD
import tw.ktrssreader.test.common.ChannelItemTestData.RSS_CHANNEL_PARTIAL_IMAGE
import tw.ktrssreader.test.common.ChannelItemTestData.RSS_CHANNEL_PARTIAL_TEXT_IMAGE
import tw.ktrssreader.test.common.ChannelItemTestData.RSS_FOLDER
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class RssStandardParserLocalTest {

    @RunWith(Parameterized::class)
    class RssStandardParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: RssStandardChannelData?
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("$RSS_FOLDER/rss_v2_full.xml", FULL_RSS_CHANNEL),
                arrayOf("$RSS_FOLDER/rss_v2_has_non_channel_attrs.xml", FULL_RSS_CHANNEL),
                arrayOf(
                    "$RSS_FOLDER/rss_v2_has_non_channel_attrs_follow_behind.xml",
                    FULL_RSS_CHANNEL
                ),
                arrayOf("$RSS_FOLDER/rss_v2_has_non_item_attrs.xml", FULL_RSS_CHANNEL),
                arrayOf(
                    "$RSS_FOLDER/rss_v2_has_non_item_attrs_follow_behind.xml",
                    FULL_RSS_CHANNEL
                ),
                arrayOf("$RSS_FOLDER/rss_v2_some_channel_attrs_missing.xml", PARTIAL_RSS_CHANNEL),
                arrayOf(
                    "$RSS_FOLDER/rss_v2_some_channel_image_attrs_missing.xml",
                    RSS_CHANNEL_PARTIAL_IMAGE
                ),
                arrayOf(
                    "$RSS_FOLDER/rss_v2_some_channel_textInput_attrs_missing.xml",
                    RSS_CHANNEL_PARTIAL_TEXT_IMAGE
                ),
                arrayOf(
                    "$RSS_FOLDER/rss_v2_some_channel_cloud_attrs_missing.xml",
                    RSS_CHANNEL_PARTIAL_CLOUD
                ),
            )
        }

        private val rssStandardParser: RssStandardParser = RssStandardParser()

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = rssStandardParser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }
}