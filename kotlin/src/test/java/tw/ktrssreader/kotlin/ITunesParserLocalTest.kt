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
import tw.ktrssreader.kotlin.model.channel.ITunesChannelData
import tw.ktrssreader.kotlin.parser.ITunesParser
import tw.ktrssreader.test.common.ChannelItemTestData
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class ITunesParserLocalTest {

    @RunWith(Parameterized::class)
    class ITunesParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: ITunesChannelData
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("${ChannelItemTestData.ITUNES_FOLDER}/itunes_rss_v2_full.xml",
                    ChannelItemTestData.FULL_ITUNES_CHANNEL
                ),
                arrayOf("${ChannelItemTestData.ITUNES_FOLDER}/itunes_rss_v2_some_channel_attrs_missing.xml",
                    ChannelItemTestData.PARTIAL_ITUNES_CHANNEL
                ),
                arrayOf("${ChannelItemTestData.ITUNES_FOLDER}/itunes_rss_v2_without_itunes_attributes.xml",
                    ChannelItemTestData.PARTIAL_ITUNES_CHANNEL_2
                ),
            )
        }

        private val parser: ITunesParser = ITunesParser()

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = parser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }

    @RunWith(Parameterized::class)
    class ITunesErrorTagParserErrorTagTest(private val rssFilePath: String): ErrorTagParserBaseTest() {

        @Test(expected = SAXParseException::class)
        fun parse() {
            val parser = ITunesParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            parser.parse(xml)
        }
    }
}