package tw.ktrssreader.parser

import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.test.common.ChannelItemTestData.GOOGLE_PLAY_FOLDER
import tw.ktrssreader.kotlin.model.channel.GoogleChannelData
import tw.ktrssreader.test.common.ChannelItemTestData.FULL_GOOGLE_CHANNEL
import tw.ktrssreader.test.common.ChannelItemTestData.PARTIAL_GOOGLE_CHANNEL
import tw.ktrssreader.test.common.ChannelItemTestData.PARTIAL_GOOGLE_CHANNEL_2
import tw.ktrssreader.parser.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class AndroidGoogleParserTest {

    @RunWith(Parameterized::class)
    class GoogleParserParseFunctionTest(
        private val rssFilePath: String,
        private val expectedChannel: GoogleChannelData
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("$GOOGLE_PLAY_FOLDER/google_play_rss_v2_full.xml", FULL_GOOGLE_CHANNEL),
                arrayOf("$GOOGLE_PLAY_FOLDER/google_play_rss_v2_without_google_attrs.xml", PARTIAL_GOOGLE_CHANNEL),
                arrayOf("$GOOGLE_PLAY_FOLDER/google_play_rss_v2_some_channel_attrs_missing.xml", PARTIAL_GOOGLE_CHANNEL_2),
                )
        }

        private val parser: AndroidGoogleParser = AndroidGoogleParser()

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = parser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }


    @RunWith(Parameterized::class)
    class GoogleErrorTagParserErrorTagTest(private val rssFilePath: String): ErrorTagParserBaseTest() {

        @Test(expected = XmlPullParserException::class)
        fun parse() {
            val parser = AndroidGoogleParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            parser.parse(xml)
        }
    }
}