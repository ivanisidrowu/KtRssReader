package tw.ktrssreader.database

import org.junit.Test
import tw.ktrssreader.constant.Const
import tw.ktrssreader.database.base.DaoTestBase
import tw.ktrssreader.persistence.db.entity.ChannelEntity
import tw.ktrssreader.test.common.shouldBe
import tw.ktrssreader.test.common.shouldNotBe
import java.util.*

class ChannelDaoTest : DaoTestBase() {

    private val fakeTime = Calendar.getInstance().timeInMillis
    private val fakeType = Const.RSS_STANDARD
    private val fakeChannelByteArray = byteArrayOf()

    @Test
    fun testGetChannelByUrlSuccessfully() {
        val subject = database.channelDao()
        val fakeUrl = "fakeUrl"
        val expected = ChannelEntity(fakeUrl, fakeType, fakeChannelByteArray, fakeTime)
        subject.insert(expected)

        val actual = subject.getChannel(fakeUrl, Const.RSS_STANDARD)

        actual shouldBe expected
    }

    @Test
    fun testGetChannelByUrlFailed() {
        val subject = database.channelDao()
        val expected = ChannelEntity("fakeUrl", fakeType, fakeChannelByteArray, fakeTime)
        subject.insert(expected)

        val actual = subject.getChannel("wrongUrl", Const.RSS_STANDARD)

        actual shouldBe null
    }

    @Test
    fun testClearCache() {
        val fakeUrl1 = "fakeUrl1"
        val fakeUrl2 = "fakeUrl2"
        val fakeUrl3 = "fakeUrl3"
        val subject = database.channelDao()
        val entity1 = ChannelEntity(fakeUrl1, fakeType, fakeChannelByteArray, fakeTime)
        val entity2 = ChannelEntity(fakeUrl2, fakeType, fakeChannelByteArray, fakeTime)
        val entity3 = ChannelEntity(fakeUrl3, fakeType, fakeChannelByteArray, fakeTime)
        subject.insert(entity1)
        subject.insert(entity2)
        subject.insert(entity3)

        subject.getChannel(fakeUrl1, fakeType) shouldNotBe null
        subject.getChannel(fakeUrl2, fakeType) shouldNotBe null
        subject.getChannel(fakeUrl3, fakeType) shouldNotBe null
        subject.clearAll()
        subject.getChannel(fakeUrl1, fakeType) shouldBe null
        subject.getChannel(fakeUrl2, fakeType) shouldBe null
        subject.getChannel(fakeUrl3, fakeType) shouldBe null
    }

    @Test
    fun testDeleteCacheByUrl() {
        val fakeUrl1 = "fakeUrl1"
        val fakeUrl2 = "fakeUrl2"
        val subject = database.channelDao()
        val entity1 = ChannelEntity(fakeUrl1, fakeType, fakeChannelByteArray, fakeTime)
        val entity2 = ChannelEntity(fakeUrl2, fakeType, fakeChannelByteArray, fakeTime)

        subject.insert(entity1)
        subject.insert(entity2)
        subject.deleteByUrl(fakeUrl1)
        subject.getChannel(fakeUrl1, fakeType) shouldBe null
        subject.getChannel(fakeUrl2, fakeType) shouldNotBe null
    }
}