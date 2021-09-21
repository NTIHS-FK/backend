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
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/api/sign-up") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Gson().toJson(
                        mapOf("name" to "a", "email" to "aa", "password" to "aaaaaa")
                    )
                )
            }
        }.apply {
            val data = Gson().fromJson(response.content, APIData::class.java)
            println(response.content)
            TestCase.assertFalse(data.error)
        }
    }
}