package extensions

import io.mockk.mockk
import org.junit.Assert

inline fun <reified T : Any> mockkRelaxed() = mockk<T>(relaxed = true)

infix fun Any?.shouldBe(expect: Any?) = Assert.assertEquals(expect, this)

infix fun Any?.shouldNotBe(expect: Any?) = Assert.assertNotEquals(expect, this)