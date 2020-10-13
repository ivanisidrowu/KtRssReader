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

import net.ettoday.test.common.XmlFileReader
import net.ettoday.test.common.shouldBe
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import tw.ktrssreader.generated.RssDataParser
import tw.ktrssreader.processor.test.data.TestData

@RunWith(Enclosed::class)
class RssTagParserTest {

    @RunWith(Parameterized::class)
    class RssStandardParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: RssData?
    ) {
        // TODO: More tests.
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("RSS/rss_v2_full.xml", TestData.RSS_DATA),
            )
        }

        private val rssStandardParser: RssDataParser = RssDataParser

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = rssStandardParser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }
}