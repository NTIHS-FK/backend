package com.ntihs_fk

import com.ntihs_fk.drawImage.defaultDraw
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import io.ktor.utils.io.streams.*
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class DrawImageAPITest {
    private val text = "\\Young 教我/\n" +
            "台南高工網頁設計社電神Young\n" +
            "幫網頁社寫了一個官網\n" +
            "很會Python的專家\n" +
            "很會社交的社交大師\n" +
            "Facebook: Yang Wang\n" +
            "Twitter: Young___TW\n" +
            "Instagram: _young_wang\n" +
            "GitHub: Young-TW\n" +
            "Blog: Young Blog\n" +
            "Contact: young20050727@gmail.com\n" +
            "'信不信我用OSU電爆你'---Young 2021.08.23"

    @Test
    fun testRequests() {
        withTestApplication({ module(testing = true) }) {
            val boundary = "WebAppBoundary"
//            val fileBytes = File("a.jpg").readBytes()

            handleRequest(HttpMethod.Post, "/api/post") {

                addHeader(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )
                setBody(boundary, listOf(
                    PartData.FormItem(text, { }, headersOf(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Inline
                            .withParameter(ContentDisposition.Parameters.Name, "text")
                            .toString()
                    )
                    ),
//                    PartData.FileItem({ fileBytes.inputStream().asInput() }, {}, headersOf(
//                        HttpHeaders.ContentDisposition,
//                        ContentDisposition.File
//                            .withParameter(ContentDisposition.Parameters.Name, "image")
//                            .withParameter(ContentDisposition.Parameters.FileName, "a.jpg")
//                            .toString()
//                    )
//                    )
                )
                )
            }.apply {
                assertEquals("{\"error\":false,\"message\":\"ok\"}", response.content)
            }
        }
    }

    @Test
    fun testDraw() {
        defaultDraw(text)
    }
}