package extensions

import io.mockk.MockKVerificationScope
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert

inline fun <reified T : Any> mockkRelaxed() = mockk<T>(relaxed = true)

infix fun Any?.shouldBe(expected: Any?) = Assert.assertEquals(expected, this)

infix fun Any?.shouldNotBe(expected: Any?) = Assert.assertNotEquals(expected, this)

fun never(block: MockKVerificationScope.() -> Unit) = verify(exactly = 0, verifyBlock = block)