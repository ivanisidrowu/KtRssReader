package tw.ktrssreader.cache

import extensions.mockkRelaxed
import extensions.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.After
import org.junit.Before
import org.junit.Test
import tw.ktrssreader.config.KtRssReaderGlobalConfig
import tw.ktrssreader.constant.Const
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.persistence.db.KtRssReaderDatabase
import tw.ktrssreader.persistence.db.dao.ChannelDao
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.convertToByteArray
import tw.ktrssreader.utils.convertToObject

class DatabaseRssCacheLocalTest {

    private lateinit var subject: DatabaseRssCache<RssStandardChannel>

    @RelaxedMockK
    private lateinit var mockDao: ChannelDao

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

        mockkStatic("tw.ktrssreader.utils.TopLevelFunctionsKt")

        subject = DatabaseRssCache()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `Read cache`() {
        val expected = mockkRelaxed<RssStandardChannel>()
        every {
            mockDao.getChannel(any(), any())?.channel?.convertToObject<RssStandardChannel>()
        } returns expected

        val actual = subject.readCache(fakeUrl, fakeType)

        actual shouldBe expected
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
}