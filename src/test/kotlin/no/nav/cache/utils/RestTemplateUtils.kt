package no.nav.cache.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

object RestTemplateUtils {

    inline fun <reified RequestBody, reified ResponseBody> TestRestTemplate.postAndAssert(
        uri: String,
        request: HttpEntity<RequestBody>,
        expectedStatus: HttpStatus
    ): ResponseBody {
        val response = exchange(uri, HttpMethod.POST, request, ResponseBody::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
        return response.body!!
    }

    inline fun <reified RequestBody> TestRestTemplate.deleteAndAssert(
        uri: String,
        request: HttpEntity<RequestBody>,
        expectedStatus: HttpStatus
    ) {
        val response = exchange(uri, HttpMethod.DELETE, request, Unit::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
    }

    inline fun <reified RequestBody, reified ResponseBody> TestRestTemplate.getAndAssert(
        uri: String,
        request: HttpEntity<RequestBody>,
        expectedStatus: HttpStatus,
        expectedBody: ResponseBody?
    ): ResponseBody? {
        val response = exchange(uri, HttpMethod.GET, request, ResponseBody::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
        val body = response.body
        if (expectedBody !is Unit) assertThat(body).isEqualTo(expectedBody)
        return body
    }

    inline fun <reified RequestBody, reified ResponseBody : Any> TestRestTemplate.putAndAssert(
        uri: String,
        request: HttpEntity<RequestBody>,
        expectedStatus: HttpStatus,
        expectedBody: ResponseBody?
    ): ResponseBody? {
        val response = exchange(uri, HttpMethod.PUT, request, ResponseBody::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
        val body = response.body
        if (expectedBody !== null) assertThat(body).isEqualTo(expectedBody)
        return body
    }
}
