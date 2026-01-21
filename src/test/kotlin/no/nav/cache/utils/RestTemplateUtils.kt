package no.nav.cache.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.json.JSONObject
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity

object RestTemplateUtils {

    inline fun <reified RequestBody : Any, reified ResponseBody : Any> TestRestTemplate.postAndAssert(
        request: RequestEntity<RequestBody>,
        expectedStatus: HttpStatus
    ): ResponseBody {
        val response = exchange(request, ResponseBody::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
        return response.body!!
    }

    fun TestRestTemplate.deleteAndAssert(
        request: RequestEntity<Void>,
        expectedStatus: HttpStatus
    ) {
        val response = exchange(request, Any::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
    }

    inline fun <reified ResponseBody : Any> TestRestTemplate.getAndAssert(
        request: RequestEntity<Void>,
        expectedStatus: HttpStatus,
        expectedBody: ResponseBody?
    ): ResponseBody? {
        val response = exchange(request, ResponseBody::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
        val body = response.body
        if (expectedBody !is Unit) JSONAssert.assertEquals(JSONObject(expectedBody), JSONObject(body), false)
        return body
    }

    inline fun <reified RequestBody : Any, reified ResponseBody : Any> TestRestTemplate.putAndAssert(
        request: RequestEntity<RequestBody>,
        expectedStatus: HttpStatus,
        expectedBody: ResponseBody?
    ): ResponseBody? {
        val response = exchange(request, ResponseBody::class.java)
        assertThat(response.statusCode).isEqualTo(expectedStatus)
        val body = response.body
        if (expectedBody !== null) assertThat(body).isEqualTo(expectedBody)
        return body
    }
}
