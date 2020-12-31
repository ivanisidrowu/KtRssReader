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
import tw.ktrssreader.kotlin.model.channel.GoogleChannelData
import tw.ktrssreader.kotlin.parser.GoogleParser
import tw.ktrssreader.test.common.ChannelItemTestData
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class GoogleParserLocalTest {

    @RunWith(Parameterized::class)
    class GoogleParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: GoogleChannelData
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("${ChannelItemTestData.GOOGLE_PLAY_FOLDER}/google_play_rss_v2_full.xml",
                    ChannelItemTestData.FULL_GOOGLE_CHANNEL
                ),
                arrayOf("${ChannelItemTestData.GOOGLE_PLAY_FOLDER}/google_play_rss_v2_without_google_attrs.xml",
                    ChannelItemTestData.PARTIAL_GOOGLE_CHANNEL
                ),
                arrayOf("${ChannelItemTestData.GOOGLE_PLAY_FOLDER}/google_play_rss_v2_some_channel_attrs_missing.xml",
                    ChannelItemTestData.PARTIAL_GOOGLE_CHANNEL_2
                ),
            )
        }

        private val parser: GoogleParser = GoogleParser()

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = parser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }


    @RunWith(Parameterized::class)
    class GoogleErrorTagParserErrorTagTest(private val rssFilePath: String): ErrorTagParserBaseTest() {

        @Test(expected = SAXParseException::class)
        fun parse() {
            val parser = GoogleParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            parser.parse(xml)
        }
    }
}
