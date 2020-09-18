package tw.ktrssreader

import extensions.mockkRelaxed
import extensions.never
import extensions.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.After
import org.junit.Before
import org.junit.Test
import tw.ktrssreader.cache.DatabaseRssCache
import tw.ktrssreader.fetcher.XmlFetcher
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.parser.RssStandardParser
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.ThreadUtils

class KtRssReaderLocalTest {

    private val fakeUrl = "fakeUrl"
    private val fakeXmlContent = "fakeXmlContent"

    @RelaxedMockK
    private lateinit var mockRssCache: DatabaseRssCache<RssStandardChannel>

    @RelaxedMockK
    private lateinit var mockFetcher: XmlFetcher

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkObject(ThreadUtils)
        mockkObject(KtRssProvider)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test(expected = Exception::class)
    fun `Get channel on main thread and return error`() {
        every { ThreadUtils.isMainThread() } returns true

        reader<RssStandardChannel>(fakeUrl)
    }

    @Test
    fun `Get remote channel successfully`() {
        val expected = mockkRelaxed<RssStandardChannel>()
        every { ThreadUtils.isMainThread() } returns false
        every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
        every { mockRssCache.readCache(fakeUrl) } returns null
        every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
        every { mockFetcher.fetch(url = fakeUrl, charset = any()) } returns fakeXmlContent
        mockkConstructor(RssStandardParser::class)
        every { anyConstructed<RssStandardParser>().parse(fakeXmlContent) } returns expected

        val actual = reader<RssStandardChannel>(fakeUrl) {
            useRemote = true
        }

        never { mockRssCache.readCache(fakeUrl) }
        verify { mockRssCache.saveCache(fakeUrl, expected) }
        actual shouldBe expected
    }

    @Test(expected = Exception::class)
    fun `Get remote channel failed`() {
        every { ThreadUtils.isMainThread() } returns false
        every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
        every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
        every { mockFetcher.fetch(url = fakeUrl, charset = any()) } throws Exception()

        reader<RssStandardChannel>(fakeUrl) {
            useRemote = true
        }
    }

    @Test
    fun `Get cache channel successfully`() {
        val expected = mockkRelaxed<RssStandardChannel>()
        every { ThreadUtils.isMainThread() } returns false
        every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
        every { mockRssCache.readCache(fakeUrl) } returns expected

        val actual = reader<RssStandardChannel>(fakeUrl)

        actual shouldBe expected
    }

    @Test
    fun `Gel cache channel but cache does not exist`() {
        val expected = mockkRelaxed<RssStandardChannel>()
        every { ThreadUtils.isMainThread() } returns false
        every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
        every { mockRssCache.readCache(fakeUrl) } returns null
        every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
        every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
        mockkConstructor(RssStandardParser::class)
        every { anyConstructed<RssStandardParser>().parse(fakeXmlContent) } returns expected

        val actual = reader<RssStandardChannel>(fakeUrl)

        verify { mockRssCache.saveCache(fakeUrl, expected) }
        actual shouldBe expected
    }
}