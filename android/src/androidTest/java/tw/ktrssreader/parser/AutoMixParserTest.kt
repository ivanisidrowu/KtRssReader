package tw.ktrssreader.parser

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.xmlpull.v1.XmlPullParserException
import tw.ktrssreader.kotlin.model.channel.AutoMixChannelData
import tw.ktrssreader.parser.ChannelItemTestData.AUTOMIX_FOLDER
import tw.ktrssreader.parser.ChannelItemTestData.CATEGORY_NO_ORDERING_AUTOMIX_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.FULL_AUTOMIX_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.NESTED_ITUNES_CATEGORY_AUTOMIX_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.PARTIAL_AUTOMIX_CHANNEL
import tw.ktrssreader.parser.ChannelItemTestData.PARTIAL_AUTOMIX_CHANNEL_2
import tw.ktrssreader.parser.base.ErrorTagParserBaseTest
import tw.ktrssreader.test.common.XmlFileReader
import tw.ktrssreader.test.common.shouldBe

class AutoMixParserTest {

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
                    "${AUTOMIX_FOLDER}/rss_v2_itunes_google_full.xml",
                    FULL_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "$AUTOMIX_FOLDER/rss_v2_itunes_google_category_no_ordering.xml",
                    CATEGORY_NO_ORDERING_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${AUTOMIX_FOLDER}/rss_v2_itunes_google_nested_itunes_category.xml",
                    NESTED_ITUNES_CATEGORY_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${AUTOMIX_FOLDER}/rss_v2_itunes_google_without_items.xml",
                    PARTIAL_AUTOMIX_CHANNEL
                ),
                arrayOf(
                    "${AUTOMIX_FOLDER}/rss_v2_itunes_google_without_itunes_attrs.xml",
                    PARTIAL_AUTOMIX_CHANNEL_2
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

        @Test(expected = XmlPullParserException::class)
        fun parse() {
            val parser = AutoMixParser()
            val xml = XmlFileReader.readFile(rssFilePath)

            parser.parse(xml)
        }
    }
}