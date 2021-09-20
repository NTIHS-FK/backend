package com.ntihs_fk

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase
import org.junit.Test

data class APIData(val error: Boolean)

class LoginTest {
    @Test
    fun login() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/api/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("{\n" +
                        "\"username\": \"jetbrains\"" +
                        "}"
                )
            }
        }.apply {
            val data = Gson().fromJson(response.content, APIData::class.java)
            TestCase.assertFalse(data.error)
        }
    }
}