package tw.ktrssreader.fetcher

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Test
import tw.ktrssreader.network.OkHttpRequest
import tw.ktrssreader.provider.KtRssProvider
import net.ettoday.test.common.mockkRelaxed

class XmlFetcherLocalTest {

    @Test
    fun `Fetch specific url`() {
        val subject = XmlFetcher()
        val mockOkHttpRequest = mockkRelaxed<OkHttpRequest>()
        mockkObject(KtRssProvider)
        every { KtRssProvider.providerOkHttpRequest(any(), any()) } returns mockOkHttpRequest
        val fakeUrl = "fakeUrl"
        val fakeCharset = Charsets.UTF_8

        subject.fetch(fakeUrl, fakeCharset)

        verify {
            mockOkHttpRequest.get(fakeUrl, fakeCharset)
        }
    }
}