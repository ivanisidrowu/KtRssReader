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
        val expectEntity = ChannelEntity(fakeUrl, byteArrayOf(), System.currentTimeMillis())
        subject.insert(expectEntity)

        val actual = subject.getChannelByUrl(fakeUrl)

        actual?.url shouldBe expectEntity.url
        actual?.channel.contentEquals(expectEntity.channel) shouldBe true
        actual?.time shouldBe expectEntity.time
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