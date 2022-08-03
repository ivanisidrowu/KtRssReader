package tw.ktrssreader

import app.cash.turbine.test
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import tw.ktrssreader.cache.DatabaseRssCache
import tw.ktrssreader.constant.Const
import tw.ktrssreader.fetcher.XmlFetcher
import tw.ktrssreader.kotlin.model.channel.RssStandardChannel
import tw.ktrssreader.kotlin.model.channel.RssStandardChannelData
import tw.ktrssreader.parser.AndroidRssStandardParser
import tw.ktrssreader.provider.KtRssProvider
import tw.ktrssreader.test.common.mockkRelaxed
import tw.ktrssreader.test.common.never
import tw.ktrssreader.test.common.shouldBe
import tw.ktrssreader.utils.ThreadUtils

@RunWith(Enclosed::class)
class KtRssReaderLocalTest {

    abstract class ReaderTestBase {

        protected val fakeUrl = "fakeUrl"
        protected val fakeType = Const.RSS_STANDARD
        private val fakeXmlContent = "fakeXmlContent"

        @RelaxedMockK
        protected lateinit var mockRssCache: DatabaseRssCache<RssStandardChannel>

        @RelaxedMockK
        protected lateinit var mockFetcher: XmlFetcher

        @RelaxedMockK
        protected lateinit var mockException: Exception

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
            val expected = mockkRelaxed<RssStandardChannelData>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl, fakeType, any()) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(url = fakeUrl, charset = any()) } returns fakeXmlContent
            mockkConstructor(AndroidRssStandardParser::class)
            every { anyConstructed<AndroidRssStandardParser>().parse(fakeXmlContent) } returns expected
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

        protected fun mockGetCacheChannelSuccessfully(block: (RssStandardChannel) -> Unit,) {
            val expected = mockkRelaxed<RssStandardChannelData>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl, fakeType, any()) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
            mockkConstructor(AndroidRssStandardParser::class)
            every { anyConstructed<AndroidRssStandardParser>().parse(fakeXmlContent) } returns expected

            block(expected)
        }

        protected fun mockGetCacheChannelFailed(block: (RssStandardChannel) -> Unit) {
            val expected = mockkRelaxed<RssStandardChannelData>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl, fakeType, any()) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
            mockkConstructor(AndroidRssStandardParser::class)
            every { anyConstructed<AndroidRssStandardParser>().parse(fakeXmlContent) } returns expected
            every { ThreadUtils.runOnNewThread(any(), any()) } answers {
                mockRssCache.saveCache(fakeUrl, expected)
            }

            block(expected)
        }

        protected fun mockFlushCache(block: (RssStandardChannel) -> Unit) {
            val expected = mockkRelaxed<RssStandardChannelData>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl, fakeType, any()) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
            mockkConstructor(AndroidRssStandardParser::class)
            every { anyConstructed<AndroidRssStandardParser>().parse(fakeXmlContent) } returns expected

            block(expected)
        }

        protected fun mockFetchDataSuccessfullyButSaveCacheFailed(block: (RssStandardChannel) -> Unit) {
            val expected = mockkRelaxed<RssStandardChannelData>()
            every { ThreadUtils.isMainThread() } returns false
            every { KtRssProvider.provideRssCache<RssStandardChannel>() } returns mockRssCache
            every { mockRssCache.readCache(fakeUrl, fakeType, any()) } returns null
            every { KtRssProvider.provideXmlFetcher() } returns mockFetcher
            every { mockFetcher.fetch(any(), any()) } returns fakeXmlContent
            mockkConstructor(AndroidRssStandardParser::class)
            every { anyConstructed<AndroidRssStandardParser>().parse(fakeXmlContent) } returns expected
            every { mockRssCache.saveCache(fakeUrl, expected) } throws mockException

            block(expected)
        }
    }

    class ReadTest : ReaderTestBase() {
        @Test(expected = Exception::class)
        fun `Get channel on main thread and return error`() {
            every { ThreadUtils.isMainThread() } returns true

            Reader.read<RssStandardChannel>(fakeUrl)
        }

        @Test
        fun `Get remote channel successfully`() = mockGetRemoteChannelSuccessfully { mockItem ->
            val actual = Reader.read<RssStandardChannel>(fakeUrl) {
                useCache = false
            }

            never {
                mockRssCache.readCache(fakeUrl, fakeType, any())
                mockRssCache.saveCache(fakeUrl, mockItem)
            }
            actual shouldBe mockItem
        }

        @Test(expected = Exception::class)
        fun `Get remote channel failed`() = mockGetRemoteChannelFailed {
            Reader.read<RssStandardChannel>(fakeUrl) {
                useCache = false
            }
        }

        @Test
        fun `Get cache channel successfully`() = mockGetCacheChannelSuccessfully { mockItem ->
            val actual = Reader.read<RssStandardChannel>(fakeUrl)

            actual shouldBe mockItem
        }

        @Test
        fun `Get cache channel failed`() = mockGetCacheChannelFailed { mockItem ->
            val actual = Reader.read<RssStandardChannel>(fakeUrl)

            verify { mockRssCache.saveCache(fakeUrl, mockItem) }
            actual shouldBe mockItem
        }

        @Test
        fun `Flush cache successfully`() = mockFlushCache { mockItem ->
            val actual = Reader.read<RssStandardChannel>(fakeUrl) {
                flushCache = true
            }

            verify { mockRssCache.removeCache(fakeUrl) }
            actual shouldBe mockItem
        }

        @Test
        fun `Flush cache failed`() = mockFlushCache { mockItem ->
            every { mockRssCache.removeCache(fakeUrl) } throws mockException

            val actual = Reader.read<RssStandardChannel>(fakeUrl) {
                flushCache = true
            }

            verify {
                mockRssCache.removeCache(fakeUrl)
                mockException.printStackTrace()
            }
            actual shouldBe mockItem
        }

        @Test
        fun `Fetch data successfully but save cache failed`() {
            mockFetchDataSuccessfullyButSaveCacheFailed { mockItem ->
                val actual = Reader.read<RssStandardChannel>(fakeUrl)
                verify { mockException.printStackTrace() }
                actual shouldBe mockItem
            }
        }
    }

    class FlowReadTest : ReaderTestBase() {

        @Test
        fun `Get channel on main thread and return error`() = runBlocking {
            every { ThreadUtils.isMainThread() } returns true

            Reader.flowRead<RssStandardChannel>(fakeUrl)
                .test {
                    expectError()
                }
        }

        @Test
        fun `Get remote channel successfully`() = mockGetRemoteChannelSuccessfully { mockItem ->
            runBlocking {
                Reader.flowRead<RssStandardChannel>(fakeUrl) {
                    useCache = false
                }.test {
                    mockItem shouldBe expectItem()
                    expectComplete()
                }
            }
        }

        @Test
        fun `Get remote channel failed`() = mockGetRemoteChannelFailed {
            runBlocking {
                Reader.flowRead<RssStandardChannel>(fakeUrl) {
                    useCache = false
                }.test {
                    expectError()
                }
            }
        }

        @Test
        fun `Get cache channel successfully`() = mockGetCacheChannelSuccessfully { mockItem ->
            runBlocking {
                Reader.flowRead<RssStandardChannel>(fakeUrl)
                    .test {
                        mockItem shouldBe expectItem()
                        expectComplete()
                    }
            }
        }

        @Test
        fun `Get cache channel failed`() = mockGetCacheChannelFailed { mockItem ->
            runBlocking {
                Reader.flowRead<RssStandardChannel>(fakeUrl)
                    .test {
                        verify { mockRssCache.saveCache(fakeUrl, mockItem) }
                        mockItem shouldBe expectItem()
                        expectComplete()
                    }
            }
        }

        @Test
        fun `Flush cache successfully`() = mockFlushCache { mockItem ->
            runBlocking {
                Reader.flowRead<RssStandardChannel>(fakeUrl) {
                    flushCache = true
                }.test {
                    verify { mockRssCache.removeCache(fakeUrl) }
                    mockItem shouldBe expectItem()
                    expectComplete()
                }
            }
        }

        @Test
        fun `Flush cache failed`() = mockFlushCache { mockItem ->
            runBlocking {
                every { mockRssCache.removeCache(fakeUrl) } throws mockException

                Reader.flowRead<RssStandardChannel>(fakeUrl) {
                    flushCache = true
                }.test {
                    verify {
                        mockRssCache.removeCache(fakeUrl)
                        mockException.printStackTrace()
                    }
                    mockItem shouldBe expectItem()
                    expectComplete()
                }
            }
        }

        @Test
        fun `Fetch data successfully but save cache failed`() {
            mockFetchDataSuccessfullyButSaveCacheFailed { mockItem ->
                runBlocking {
                    Reader.flowRead<RssStandardChannel>(fakeUrl)
                        .test {
                            verify { mockException.printStackTrace() }
                            mockItem shouldBe expectItem()
                            expectComplete()
                        }
                }
            }
        }
    }

    class CoroutineReadTest : ReaderTestBase() {

        @Test(expected = Exception::class)
        fun `Get channel on main thread and return error`() = runBlocking<Unit> {
            every { ThreadUtils.isMainThread() } returns true

            Reader.coRead<RssStandardChannel>(fakeUrl)
        }

        @Test
        fun `Get remote channel successfully`() = mockGetRemoteChannelSuccessfully { mockItem ->
            runBlocking {
                val actual = Reader.coRead<RssStandardChannel>(fakeUrl) {
                    useCache = false
                }
                actual shouldBe mockItem
            }
        }

        @Test(expected = Exception::class)
        fun `Get remote channel failed`() = mockGetRemoteChannelFailed {
            runBlocking {
                Reader.coRead<RssStandardChannel>(fakeUrl) {
                    useCache = false
                }
            }
        }

        @Test
        fun `Get cache channel successfully`() = mockGetCacheChannelSuccessfully { mockItem ->
            runBlocking {
                val actual = Reader.coRead<RssStandardChannel>(fakeUrl)

                actual shouldBe mockItem
            }
        }

        @Test
        fun `Get cache channel failed`() = mockGetCacheChannelFailed { mockItem ->
            runBlocking {
                val actual = Reader.coRead<RssStandardChannel>(fakeUrl)

                verify { mockRssCache.saveCache(fakeUrl, mockItem) }
                actual shouldBe mockItem
            }
        }

        @Test
        fun `Flush cache successfully`() = mockFlushCache { mockItem ->
            runBlocking {
                val actual = Reader.coRead<RssStandardChannel>(fakeUrl) {
                    flushCache = true
                }

                verify { mockRssCache.removeCache(fakeUrl) }
                actual shouldBe mockItem
            }
        }

        @Test
        fun `Flush cache failed`() = mockFlushCache { mockItem ->
            runBlocking {
                every { mockRssCache.removeCache(fakeUrl) } throws mockException

                val actual = Reader.coRead<RssStandardChannel>(fakeUrl) {
                    flushCache = true
                }

                verify {
                    mockRssCache.removeCache(fakeUrl)
                    mockException.printStackTrace()
                }
                actual shouldBe mockItem
            }
        }

        @Test
        fun `Fetch data successfully but save cache failed`() {
            mockFetchDataSuccessfullyButSaveCacheFailed { mockItem ->
                runBlocking {
                    val actual = Reader.coRead<RssStandardChannel>(fakeUrl)
                    verify { mockException.printStackTrace() }
                    actual shouldBe mockItem
                }
            }
        }
    }
}
