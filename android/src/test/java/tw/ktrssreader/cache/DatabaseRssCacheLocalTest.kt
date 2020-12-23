package tw.ktrssreader.cache

import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.After
import org.junit.Before
import org.junit.Test
import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.constant.Const
import tw.ktrssreader.kotlin.model.channel.RssStandardChannel
import tw.ktrssreader.persistence.db.KtRssReaderDatabase
import tw.ktrssreader.persistence.db.dao.ChannelDao
import tw.ktrssreader.persistence.db.entity.ChannelEntity
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.test.common.mockkRelaxed
import tw.ktrssreader.test.common.shouldBe
import tw.ktrssreader.utils.convertToByteArray
import tw.ktrssreader.utils.convertToObject
import java.util.*

class DatabaseRssCacheLocalTest {

    private lateinit var subject: DatabaseRssCache<RssStandardChannel>

    @RelaxedMockK
    private lateinit var mockDao: ChannelDao

    @RelaxedMockK
    private lateinit var mockChannelEntity: ChannelEntity

    private val fakeUrl = "fakeUrl"
    private val fakeType = Const.RSS_STANDARD

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkObject(KtRssReaderGlobalConfig)
        every { KtRssReaderGlobalConfig.getApplicationContext() } returns mockkRelaxed()

        val mockDatabase = mockkRelaxed<KtRssReaderDatabase>()
        mockkObject(KtRssProvider)
        every { KtRssProvider.provideDatabase(any()) } returns mockDatabase
        every { mockDatabase.channelDao() } returns mockDao
        every { mockDao.getChannel(fakeUrl, fakeType) } returns mockChannelEntity

        mockkStatic("tw.ktrssreader.utils.TopLevelFunctionsKt")

        subject = DatabaseRssCache()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `Read cache`() {
        val mockCalendar = mockkRelaxed<Calendar>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns mockCalendar
        every { mockCalendar.timeInMillis } returns 100
        every { mockChannelEntity.time } returns 10

        val expected = mockkRelaxed<RssStandardChannel>()
        every {
            mockChannelEntity.channel.convertToObject<RssStandardChannel>()
        } returns expected

        val actual = subject.readCache(fakeUrl, fakeType, expiredTimeMillis = 10000)

        actual shouldBe expected
    }

    @Test
    fun `Read expired cache`() {
        val mockCalendar = mockkRelaxed<Calendar>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns mockCalendar
        every { mockCalendar.timeInMillis } returns 10000
        every { mockChannelEntity.time } returns 10

        val expected = mockkRelaxed<RssStandardChannel>()
        every {
            mockChannelEntity.channel.convertToObject<RssStandardChannel>()
        } returns expected

        val actual = subject.readCache(fakeUrl, fakeType, expiredTimeMillis = 100)

        verify { mockDao.delete(mockChannelEntity) }
        actual shouldBe null
    }

    @Test
    fun `Save cache`() {
        val mockChannel = mockkRelaxed<RssStandardChannel>()
        val fakeByteArray: ByteArray = byteArrayOf()
        every { mockChannel.convertToByteArray() } returns fakeByteArray

        subject.saveCache(fakeUrl, mockChannel)

        verify {
            mockDao.insert(match {
                it.url == fakeUrl && it.channel.contentEquals(fakeByteArray)
            })
        }
    }

    @Test
    fun `Remove cache`() {
        subject.removeCache(fakeUrl)

        verify { mockDao.deleteByUrl(fakeUrl) }
    }
}