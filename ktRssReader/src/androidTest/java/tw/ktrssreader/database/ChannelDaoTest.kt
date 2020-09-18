package tw.ktrssreader.database

import extensions.shouldBe
import org.junit.Test
import tw.ktrssreader.database.base.DaoTestBase
import tw.ktrssreader.persistence.db.entity.ChannelEntity

class ChannelDaoTest : DaoTestBase() {

    @Test
    fun testGetChannelByUrlSuccessfully() {
        val subject = database.channelDao()
        val fakeUrl = "fakeUrl"
        val expected = ChannelEntity(fakeUrl, byteArrayOf(), System.currentTimeMillis())
        subject.insert(expected)

        val actual = subject.getChannelByUrl(fakeUrl)

        actual shouldBe expected
    }

    @Test
    fun testGetChannelByUrlFailed() {
        val subject = database.channelDao()
        val expectEntity = ChannelEntity("fakeUrl", byteArrayOf(), System.currentTimeMillis())
        subject.insert(expectEntity)

        val actual = subject.getChannelByUrl("wrongUrl")

        actual shouldBe null
    }
}