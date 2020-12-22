package tw.ktrssreader.parser.base

import org.junit.runners.Parameterized
import tw.ktrssreader.parser.ChannelItemTestData.MISMATCH_TAG_FOLDER
import tw.ktrssreader.parser.ChannelItemTestData.RSS_FOLDER

open class ErrorTagParserBaseTest {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun getErrorTagTestingData() = listOf(
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_channel_end_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_channel_start_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_channel_sub_attr_end_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_channel_sub_attr_start_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_item_start_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_item_sub_attr_end_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_item_sub_attr_start_tag_missing.xml"),
            arrayOf("${RSS_FOLDER}/${MISMATCH_TAG_FOLDER}/rss_v2_items_end_tag_missing.xml"),
        )
    }
}