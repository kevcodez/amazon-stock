package de.kevcodez.amazon.stock.api

import de.kevcodez.amazon.stock.config.AmazonConfig
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.SortedMap
import java.util.TreeMap

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.ws.rs.client.ClientBuilder

class SignedRequestHelper(private val amazonConfig: AmazonConfig) {

    init {
        initSecurity()
    }

    private var secretKeySpec: SecretKeySpec? = null
    private var mac: Mac? = null

    private val currentTimestampAsString: String
        get() {
            val zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"))

            return zonedDateTime.format(DATE_TIME_FORMATTER)
        }

    private fun initSecurity() {
        val secretyKeyBytes = amazonConfig.secretKey.toByteArray(StandardCharsets.UTF_8)
        secretKeySpec = SecretKeySpec(
            secretyKeyBytes,
            HMAC_SHA256_ALGORITHM
        )
        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM)
        mac!!.init(secretKeySpec)
    }

    fun readRequest(params: MutableMap<String, String>): String {
        val requestUrl = sign(params)

        val client = ClientBuilder.newBuilder().build()
        val target = client.target(requestUrl)
        val response = target.request().get()
        return response.readEntity(String::class.java)
    }

    private fun sign(params: MutableMap<String, String>): String {
        params["AWSAccessKeyId"] = amazonConfig.accessKeyId
        params["AssociateTag"] = amazonConfig.associateTag
        params["Timestamp"] = currentTimestampAsString

        val sortedParamMap = TreeMap(params)
        val canonicalQS = canonicalize(sortedParamMap)
        val toSign = "$REQUEST_METHOD\n${amazonConfig.endpoint}\n$REQUEST_URI\n$canonicalQS"

        val hmac = hmac(mac!!, toSign)
        val sig = urlEncodeUTF8(hmac)

        return """http://${amazonConfig.endpoint}$REQUEST_URI?$canonicalQS&Signature=$sig"""
    }

    private fun hmac(mac: Mac, stringToSign: String): String {
        val data = stringToSign.toByteArray(StandardCharsets.UTF_8)
        val rawHmac = mac.doFinal(data)
        val encoder = Base64.getEncoder()
        return String(encoder.encode(rawHmac), StandardCharsets.UTF_8)
    }

    private fun canonicalize(sortedParamMap: SortedMap<String, String>): String {
        return sortedParamMap.entries.stream()
            .map { p -> urlEncodeUTF8(p.key) + "=" + urlEncodeUTF8(p.value) }
            .reduce { p1, p2 -> "$p1&$p2" }
            .orElse("")
    }

    private fun urlEncodeUTF8(s: String): String {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            throw UnsupportedOperationException(e)
        }

    }

    companion object {

        private const val HMAC_SHA256_ALGORITHM = "HmacSHA256"
        private const val REQUEST_URI = "/onca/xml"
        private const val REQUEST_METHOD = "GET"

        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

}
