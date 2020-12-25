package tw.ktrssreader.parser

import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.kotlin.model.channel.ITunesChannelData
import tw.ktrssreader.parser.ChannelItemTestData.FULL_ITUNES_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.ITUNES_FOLDER
import tw.ktrssreader.parser.ChannelItemTestData.PARTIAL_ITUNES_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.PARTIAL_ITUNES_CHANNEL_2
import tw.ktrssreader.parser.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class ITunesParserTest {

    @RunWith(Parameterized::class)
    class ITunesParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: ITunesChannelData
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("${ITUNES_FOLDER}/itunes_rss_v2_full.xml", FULL_ITUNES_CHANNEL),
                arrayOf("${ITUNES_FOLDER}/itunes_rss_v2_some_channel_attrs_missing.xml", PARTIAL_ITUNES_CHANNEL),
                arrayOf("${ITUNES_FOLDER}/itunes_rss_v2_without_itunes_attributes.xml", PARTIAL_ITUNES_CHANNEL_2),
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

        @Test(expected = XmlPullParserException::class)
        fun parse() {
            val parser = ITunesParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            parser.parse(xml)
        }
    }
}