package tw.ktrssreader.parser

import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.parser.ChannelItemTestData.FULL_RSS_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.PARTIAL_RSS_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.RSS_CHANNEL_PARTIAL_CLOUD
import tw.ktrssreader.parser.ChannelItemTestData.RSS_CHANNEL_PARTIAL_IMAGE
import tw.ktrssreader.parser.ChannelItemTestData.RSS_CHANNEL_PARTIAL_TEXT_IMAGE

@RunWith(Enclosed::class)
class RssStandardParserTest {

    @RunWith(Parameterized::class)
    class RssStandardParserTest(
        private val rssFilePath: String,
        private val expectedChannel: RssStandardChannel?
    ) {
        companion object {
            private const val RSS_FOLDER = "RSS"

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

        private lateinit var rssStandardParser: RssStandardParser

        @Before
        fun setUp() {
            rssStandardParser = RssStandardParser()
        }

        @Test
        fun parse() {
            val xml = XmlFileReader.readFile(rssFilePath)
            val channel = rssStandardParser.parse(xml)

            Assert.assertEquals(
                "channel value should be the expected value",
                expectedChannel,
                channel
            )
        }
    }


    @RunWith(Parameterized::class)
    class RssStandardParserErrorTagTest(private val rssFilePath: String) {
        companion object {
            private const val RSS_FOLDER = "RSS"
            private const val MISMATCH_TAG_FOLDER = "StartOrEndTagMismatch"

            @JvmStatic
            @Parameterized.Parameters
            fun getTestingData() = listOf(
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_channel_end_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_channel_start_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_channel_sub_attr_end_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_channel_sub_attr_start_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_item_start_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_item_sub_attr_end_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_item_sub_attr_start_tag_missing.xml"),
                arrayOf("$RSS_FOLDER/$MISMATCH_TAG_FOLDER/rss_v2_items_end_tag_missing.xml"),
            )
        }

        @Test(expected = XmlPullParserException::class)
        fun parse() {
            val rssStandardParser = RssStandardParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            rssStandardParser.parse(xml)
        }
    }
}