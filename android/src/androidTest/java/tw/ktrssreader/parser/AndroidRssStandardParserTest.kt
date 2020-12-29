package tw.ktrssreader.parser

import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.kotlin.model.channel.RssStandardChannelData
import tw.ktrssreader.parser.ChannelItemTestData.FULL_RSS_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.PARTIAL_RSS_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.RSS_CHANNEL_PARTIAL_CLOUD
import tw.ktrssreader.parser.ChannelItemTestData.RSS_CHANNEL_PARTIAL_IMAGE
import tw.ktrssreader.parser.ChannelItemTestData.RSS_CHANNEL_PARTIAL_TEXT_IMAGE
import tw.ktrssreader.parser.ChannelItemTestData.RSS_FOLDER
import tw.ktrssreader.parser.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.shouldBe

@RunWith(Enclosed::class)
class AndroidRssStandardParserTest {

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
                arrayOf("$RSS_FOLDER/rss_v2_has_non_channel_attrs_follow_behind.xml", FULL_RSS_CHANNEL),
                arrayOf("$RSS_FOLDER/rss_v2_has_non_item_attrs.xml", FULL_RSS_CHANNEL),
                arrayOf("$RSS_FOLDER/rss_v2_has_non_item_attrs_follow_behind.xml", FULL_RSS_CHANNEL),
                arrayOf("$RSS_FOLDER/rss_v2_some_channel_attrs_missing.xml", PARTIAL_RSS_CHANNEL),
                arrayOf("$RSS_FOLDER/rss_v2_some_channel_image_attrs_missing.xml", RSS_CHANNEL_PARTIAL_IMAGE),
                arrayOf("$RSS_FOLDER/rss_v2_some_channel_textInput_attrs_missing.xml", RSS_CHANNEL_PARTIAL_TEXT_IMAGE),
                arrayOf("$RSS_FOLDER/rss_v2_some_channel_cloud_attrs_missing.xml", RSS_CHANNEL_PARTIAL_CLOUD),
            )
        }

        private val rssStandardParser: AndroidRssStandardParser = AndroidRssStandardParser()

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val actualChannel = rssStandardParser.parse(xml)

            actualChannel shouldBe expectedChannel
        }
    }


    @RunWith(Parameterized::class)
    class RssStandardErrorTagParserErrorTagTest(private val rssFilePath: String): ErrorTagParserBaseTest() {

        @Test(expected = XmlPullParserException::class)
        fun parse() {
            val rssStandardParser = AndroidRssStandardParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            rssStandardParser.parse(xml)
        }
    }
}