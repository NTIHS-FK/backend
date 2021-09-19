package com.ntihs_fk

import com.ntihs_fk.drawImage.defaultDraw
import io.ktor.http.*
import io.ktor.http.content.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.ktor.utils.io.streams.*
import java.io.File

class ApplicationTest {

    @Test
    fun testRequests() {
        withTestApplication({ module(testing = true) }) {
            val boundary = "WebAppBoundary"
            val fileBytes = File("a.jpg").readBytes()

            handleRequest(HttpMethod.Post, "/api/post") {

                addHeader(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )
                setBody(boundary, listOf(
                    PartData.FormItem("鬼ㄅ", { }, headersOf(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Inline
                            .withParameter(ContentDisposition.Parameters.Name, "text")
                            .toString()
                    )),
                    PartData.FileItem({ fileBytes.inputStream().asInput() }, {}, headersOf(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.File
                            .withParameter(ContentDisposition.Parameters.Name, "image")
                            .withParameter(ContentDisposition.Parameters.FileName, "a.jpg")
                            .toString()
                    )
                    )
                )
                )
            }.apply {
                assertEquals("{\"error\":false,\"message\":\"ok\"}", response.content)
            }
        }
    }

    @Test
    fun testDraw() {
        defaultDraw()
    }
}
