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

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import tw.ktrssreader.generated.RawRssDataParser
import tw.ktrssreader.processor.test.data.TestData
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.shouldBe

class CustomParserRawDataTest {
    @RunWith(Parameterized::class)
    class CustomParserRawParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: RawRssData?
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("$MIX_FOLDER/rss_v2_itunes_google_full.xml", TestData.RAW_RSS_DATA_1),
                arrayOf("$MIX_FOLDER/rss_v2_itunes_google_category_no_ordering.xml", TestData.RAW_RSS_DATA_1),
                arrayOf("$MIX_FOLDER/rss_v2_itunes_google_without_items.xml", TestData.RAW_RSS_DATA_2),
                arrayOf("$MIX_FOLDER/rss_v2_itunes_google_without_itunes_attrs.xml", TestData.RAW_RSS_DATA_3),
            )
        }
        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = RawRssDataParser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }
}