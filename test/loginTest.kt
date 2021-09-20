package com.ntihs_fk

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test

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
            println(response.content)
        }
    }
}