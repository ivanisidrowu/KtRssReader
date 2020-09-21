package tw.ktrssreader

import app.cash.turbine.test
import extensions.mockkRelaxed
import extensions.never
import extensions.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import tw.ktrssreader.cache.DatabaseRssCache
import tw.ktrssreader.fetcher.XmlFetcher
import tw.ktrssreader.model.channel.RssStandardChannel
import tw.ktrssreader.parser.RssStandardParser
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.utils.ThreadUtils

@RunWith(Enclosed::class)
class KtRssReaderLocalTest {

    abstract class KtRssReaderTestBase {

        protected val fakeUrl = "fakeUrl"
        private val fakeXmlContent = "fakeXmlContent"

        @RelaxedMockK
        protected lateinit var mockRssCache: DatabaseRssCache<RssStandardChannel>

        @RelaxedMockK
        protected lateinit var mockFetcher: XmlFetcher

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

        protected fun mockGetRemoteChannelSuccessfully(block: (RssStandardChannel) -> Unit) {
            val expected = mockkRelaxed<RssStandardChannel>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(url = fakeUrl, charset = any()) } returns fakeXmlContent
            mockkConstructor(RssStandardParser::class)
            every { anyConstructed<RssStandardParser>().parse(fakeXmlContent) } returns expected
            every { ThreadUtils.runOnNewThread(any(), any()) } answers {
                mockRssCache.saveCache(fakeUrl, expected)
            }

            block(expected)
        }

        protected fun mockGetRemoteChannelFailed(block: () -> Unit) {
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(url = fakeUrl, charset = any()) } throws Exception()

            block()
        }

        protected fun mockGetCacheChannelSuccessfully(block: (RssStandardChannel) -> Unit, ) {
            val expected = mockkRelaxed<RssStandardChannel>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
            mockkConstructor(RssStandardParser::class)
            every { anyConstructed<RssStandardParser>().parse(fakeXmlContent) } returns expected

            block(expected)
        }

        protected fun mockGetCacheChannelFailed(block: (RssStandardChannel) -> Unit) {
            val expected = mockkRelaxed<RssStandardChannel>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
            mockkConstructor(RssStandardParser::class)
            every { anyConstructed<RssStandardParser>().parse(fakeXmlContent) } returns expected
            every { ThreadUtils.runOnNewThread(any(), any()) } answers {
                mockRssCache.saveCache(fakeUrl, expected)
            }

            block(expected)
        }
    }

    class ReaderTest : KtRssReaderTestBase() {
        @Test(expected = Exception::class)
        fun `Get channel on main thread and return error`() {
            every { ThreadUtils.isMainThread() } returns true

            reader<RssStandardChannel>(fakeUrl)
        }

        @Test
        fun `Get remote channel successfully`() = mockGetRemoteChannelSuccessfully { mockItem ->
            val actual = reader<RssStandardChannel>(fakeUrl) {
                useRemote = true
            }

            never { mockRssCache.readCache(fakeUrl) }
            verify { mockRssCache.saveCache(fakeUrl, mockItem) }
            actual shouldBe mockItem
        }

        @Test(expected = Exception::class)
        fun `Get remote channel failed`() = mockGetRemoteChannelFailed {
            reader<RssStandardChannel>(fakeUrl) {
                useRemote = true
            }
        }

        @Test
        fun `Get cache channel successfully`() = mockGetCacheChannelSuccessfully { mockItem ->
            val actual = reader<RssStandardChannel>(fakeUrl)

            actual shouldBe mockItem
        }

        @Test
        fun `Get cache channel failed`() = mockGetCacheChannelFailed { mockItem ->
            val actual = reader<RssStandardChannel>(fakeUrl)

            verify { mockRssCache.saveCache(fakeUrl, mockItem) }
            actual shouldBe mockItem
        }
    }

    class ReaderFlowTest : KtRssReaderTestBase() {

        @Test
        fun `Get channel on main thread and return error`() = runBlocking {
            every { ThreadUtils.isMainThread() } returns true

            readerFlow<RssStandardChannel>(fakeUrl)
                .test {
                    expectError()
                }
        }

        @Test
        fun `Get remote channel successfully`() = mockGetRemoteChannelSuccessfully { mockItem ->
            runBlocking {
                readerFlow<RssStandardChannel>(fakeUrl) {
                    useRemote = true
                }.test {
                    mockItem shouldBe expectItem()
                    expectComplete()
                }
            }
        }

        @Test
        fun `Get remote channel failed`() = mockGetRemoteChannelFailed {
            runBlocking {
                readerFlow<RssStandardChannel>(fakeUrl) {
                    useRemote = true
                }.test {
                    expectError()
                }
            }
        }

        @Test
        fun `Get cache channel successfully`() = mockGetCacheChannelSuccessfully { mockItem ->
            runBlocking {
                readerFlow<RssStandardChannel>(fakeUrl)
                    .test {
                        mockItem shouldBe expectItem()
                        expectComplete()
                    }
            }
        }

        @Test
        fun `Get cache channel failed`() = mockGetCacheChannelFailed { mockItem ->
            runBlocking {
                readerFlow<RssStandardChannel>(fakeUrl)
                    .test {
                        verify { mockRssCache.saveCache(fakeUrl, mockItem) }
                        mockItem shouldBe expectItem()
                        expectComplete()
                    }
            }
        }
    }

    class ReaderSuspendTest : KtRssReaderTestBase() {

        @Test(expected = Exception::class)
        fun `Get channel on main thread and return error`() = runBlocking<Unit> {
            every { ThreadUtils.isMainThread() } returns true

            readerSuspend<RssStandardChannel>(fakeUrl)
        }

        @Test
        fun `Get remote channel successfully`() = mockGetRemoteChannelSuccessfully { mockItem ->
            runBlocking {
                val actual = readerSuspend<RssStandardChannel>(fakeUrl) {
                    useRemote = true
                }
                actual shouldBe mockItem
            }
        }

        @Test(expected = Exception::class)
        fun `Get remote channel failed`() = mockGetRemoteChannelFailed {
            runBlocking {
                readerSuspend<RssStandardChannel>(fakeUrl) {
                    useRemote = true
                }
            }
        }

        @Test
        fun `Get cache channel successfully`() = mockGetCacheChannelSuccessfully { mockItem ->
            runBlocking {
                val actual = readerSuspend<RssStandardChannel>(fakeUrl)

                actual shouldBe mockItem
            }
        }

        @Test
        fun `Get cache channel failed`() = mockGetCacheChannelFailed { mockItem ->
            runBlocking {
                val actual = readerSuspend<RssStandardChannel>(fakeUrl)

                verify { mockRssCache.saveCache(fakeUrl, mockItem) }
                actual shouldBe mockItem
            }
        }
    }
}