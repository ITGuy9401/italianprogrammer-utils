package pizza.italianprogrammer.utils.rest

import org.apache.commons.codec.CharEncoding.UTF_8
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.util.*

/**
 * Fluent library used to generate URLs
 */
class URIResolver
/**
 * Create an instance of the factory
 *
 * @param baseUrl   the base url to start with ([java.lang.String.format] style)
 * @param variables the parameters to replace in the URL, place it in order
 * @see java.lang.String.format
 */
(baseUrl: String, vararg variables: Any) {
    private val queryParameters = ArrayList<Parameter>()
    private var baseUrl: String = ""

    init {
        log.debug("[{}] Creating a new Resolver, baseUrl {}, variables {}", clazzName, baseUrl, variables)

        if (variables.isEmpty()) {
            this.baseUrl = baseUrl
        } else {
            this.baseUrl = baseUrl.format(*variables)
        }
    }

    /**
     * Adds query parameters to the request
     *
     * @param queryParameters the query parameters to add
     * @see Parameter
     */
    fun queryParameters(vararg queryParameters: Parameter): URIResolver {
        log.debug("[{}] Adding query parameters {}", clazzName, queryParameters)
        this.queryParameters.addAll(Arrays.asList(*queryParameters))
        return this
    }

    fun queryParameter(name: String, value: Any, vararg substitutions: Any): URIResolver {
        var intValue = value.toString()

        if (substitutions.isNotEmpty()) {
            intValue = value.toString().format(*substitutions)
        }

        log.debug("[{}] Adding query parameter {}={}", clazzName, name, intValue)
        this.queryParameters.add(Parameter(name, intValue))
        return this
    }

    /**
     * Appends a portion of URL
     *
     * @param url       the url portion to append with ([java.lang.String.format] style)
     * @param variables the parameters to replace in the URL, place it in order
     * @see java.lang.String.format
     */
    fun append(url: String, vararg variables: Any): URIResolver {
        log.debug("[{}] adding portion of urlbaseUrl {}, portion {}, variables {}", clazzName, this.baseUrl, url, variables)

        if (!this.baseUrl.endsWith("/") && !url.startsWith("/")) this.baseUrl += "/"

        if (variables.isEmpty()) {
            this.baseUrl += url
        } else {
            this.baseUrl += url.format(*variables)
        }

        return this
    }

    /**
     * Provide the full URL to call
     *
     * @return the built URL
     */
    fun build(): String {
        val url = StringBuilder()

        url.append(baseUrl)
        if (queryParameters.isNotEmpty()) {
            url.append(Parameter.QUERY)
            url.append(queryParameters.joinToString(Parameter.SEPARATOR) { it.build() })
        }

        return url.toString()
    }

    override fun toString(): String = this.build()

    /**
     * Utility for handling parameters
     */
    class Parameter(val name: String, val value: Any) {

        init {
            if (StringUtils.isEmpty(name))
                throw IllegalArgumentException("the parameter name is required")
            if (StringUtils.isEmpty(value.toString()))
                throw IllegalArgumentException("the parameter value is required")
        }

        /**
         * returns the parameter with the proper formatting and already URL encoded
         *
         * @return parameter as formatted string eg. `name=Foo+Bar`
         * @see URLEncoder.encode
         */
        fun build(): String =
                "${URLEncoder.encode(name.trim(), UTF_8)}=${URLEncoder.encode(value.toString().trim(), UTF_8)}"

        override fun toString(): String = this.build()

        companion object {
            const val SEPARATOR = "&"
            const val QUERY = "?"
            const val HASH = "#"
        }

    }

    companion object {
        private val clazzName = URIResolver::class.java.simpleName
        private val log = LoggerFactory.getLogger(URIResolver::class.java)
    }
}