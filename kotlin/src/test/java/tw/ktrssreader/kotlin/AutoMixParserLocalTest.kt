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
import org.xml.sax.SAXParseException
import tw.ktrssreader.kotlin.model.channel.AutoMixChannelData
import tw.ktrssreader.kotlin.parser.AutoMixParser
import tw.ktrssreader.test.common.ChannelItemTestData
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class AutoMixParserLocalTest {

    @RunWith(Parameterized::class)
    class AutoMixParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: AutoMixChannelData
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf(
                    "${ChannelItemTestData.AUTOMIX_FOLDER}/rss_v2_itunes_google_full.xml",
                    ChannelItemTestData.FULL_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${ChannelItemTestData.AUTOMIX_FOLDER}/rss_v2_itunes_google_category_no_ordering.xml",
                    ChannelItemTestData.CATEGORY_NO_ORDERING_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${ChannelItemTestData.AUTOMIX_FOLDER}/rss_v2_itunes_google_nested_itunes_category.xml",
                    ChannelItemTestData.NESTED_ITUNES_CATEGORY_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${ChannelItemTestData.AUTOMIX_FOLDER}/rss_v2_itunes_google_without_items.xml",
                    ChannelItemTestData.PARTIAL_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${ChannelItemTestData.AUTOMIX_FOLDER}/rss_v2_itunes_google_without_itunes_attrs.xml",
                    ChannelItemTestData.PARTIAL_AUTOMIX_CHANNEL_2
                ),
            )
        }

        private val parser = AutoMixParser()

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = parser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }

    @RunWith(Parameterized::class)
    class AutoMixErrorTagParserErrorTagTest(private val rssFilePath: String) :
        ErrorTagParserBaseTest() {

        @Test(expected = SAXParseException::class)
        fun parse() {
            val parser = AutoMixParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            parser.parse(xml)
        }
    }
}