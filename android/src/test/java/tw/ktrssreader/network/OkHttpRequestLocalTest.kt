package tw.ktrssreader.network

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import tw.ktrssreader.test.common.mockkRelaxed
import tw.ktrssreader.test.common.shouldBe

class OkHttpRequestLocalTest {

    private lateinit var subject: OkHttpRequest

    @RelaxedMockK
    private lateinit var mockOkHttpClient: OkHttpClient

    @RelaxedMockK
    private lateinit var mockRequestBuilder: Request.Builder

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        subject = OkHttpRequest(mockOkHttpClient, mockRequestBuilder)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `Get url successfully when charset is null`() {
        val fakeUrl = "fakeUrl"
        val mockRequest = mockkRelaxed<Request>()
        every { mockRequestBuilder.url(fakeUrl).build() } returns mockRequest
        val mockResponse = mockkRelaxed<Response>()
        every { mockOkHttpClient.newCall(mockRequest).execute() } returns mockResponse
        val mockBody = mockkRelaxed<ResponseBody>()
        every { mockResponse.body } returns mockBody
        val expected = "fakeResponseString"
        every { mockBody.string() } returns expected

        val actual = subject.get(url = fakeUrl, charset = null)

        actual shouldBe expected
    }

    @Test
    fun `Get url successfully when charset is not null`() {
        val fakeUrl = "fakeUrl"
        val fakeCharset = Charsets.UTF_8
        val mockRequest = mockkRelaxed<Request>()
        every { mockRequestBuilder.url(fakeUrl).build() } returns mockRequest
        val mockResponse = mockkRelaxed<Response>()
        every { mockOkHttpClient.newCall(mockRequest).execute() } returns mockResponse
        val mockBody = mockkRelaxed<ResponseBody>()
        every { mockResponse.body } returns mockBody
        val expected = "fakeResponseString"
        every { mockBody.source().readString(any()) } returns expected

        val actual = subject.get(url = fakeUrl, charset = fakeCharset)

        actual shouldBe expected
    }

    @Test(expected = IOException::class)
    fun `Get url failed`() {
        val fakeUrl = "fakeUrl"
        val mockRequest = mockkRelaxed<Request>()
        every { mockRequestBuilder.url(fakeUrl).build() } returns mockRequest
        val mockResponse = mockkRelaxed<Response>()
        every { mockOkHttpClient.newCall(mockRequest).execute() } returns mockResponse
        every { mockResponse.body } returns null

        subject.get(url = fakeUrl, charset = null)
    }
}
