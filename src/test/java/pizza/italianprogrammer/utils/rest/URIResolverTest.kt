package pizza.italianprogrammer.utils.rest

import junit.framework.TestCase.assertEquals
import org.apache.commons.lang3.RandomStringUtils
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

class URIResolverTest {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)


    @Throws(Exception::class)
    private fun encode(value: String): String {
        return URLEncoder.encode(value, UTF_8.name())
    }

    @Test
    @Throws(Exception::class)
    fun checkSubstitutions() {
        val value1 = "java"
        val value2 = "jakarta"
        val value3 = "guava"

        val param1Key = "iam"
        val param2Key = "favourite"
        val param3Key = "moreinfo"

        val param1Value = "batman"
        val param2Value = "spiderman"
        val param3Value = "mr robot" + "test/%s/variable"

        val expected = "my/%s/valuable/%s/url/%s/detail?%s=%s&%s=%s&%s=%s".format(
                value1, value2, value3,
                encode(param1Key), encode(param1Value),
                encode(param2Key), encode(param2Value),
                encode(param3Key), encode(String.format(param3Value, "test")))

        val result = URIResolver("my/%s/valuable/%s/url", value1, value2)
                .append("%s/detail", value3)
                .queryParameters(URIResolver.Parameter(param1Key, param1Value))
                .queryParameter(param2Key, param2Value)
                .queryParameter(param3Key, param3Value, "test")
                .build()

        log.info("Expected string {}, actual: {}", expected, result)
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun checkNoLengthParams() {
        val param1Key = RandomStringUtils.random(1)
        val param2Key = RandomStringUtils.random(1)
        val param3Key = RandomStringUtils.random(1)

        val param1Value = RandomStringUtils.random(1)
        val param2Value = RandomStringUtils.random(1)
        val param3Value = RandomStringUtils.random(1) + "test/variable"

        val expected = "my/valuable/url/detail?%s=%s&%s=%s&%s=%s".format(
                encode(param1Key), encode(param1Value),
                encode(param2Key), encode(param2Value),
                encode(param3Key), encode(param3Value))

        val result = URIResolver("my/valuable/url")
                .append("detail")
                .queryParameters(URIResolver.Parameter(param1Key, param1Value))
                .queryParameter(param2Key, param2Value)
                .queryParameter(param3Key, param3Value, "test")
                .build()

        assertEquals(expected, result)
    }
}
