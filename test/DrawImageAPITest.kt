package com.ntihs_fk

import com.ntihs_fk.cli.Main
import com.ntihs_fk.drawImage.defaultDraw
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.*

class DrawImageAPITest {
    private val text = "\\Young 教我/" +
            "台南高工網頁設計社電神Young" +
            "幫網頁社寫了一個官網" +
            "很會Python的專家" +
            "很會社交的社交大師" +
            "Facebook: Yang Wang" +
            "Twitter: Young___TW" +
            "Instagram: _young_wang" +
            "GitHub: Young-TW" +
            "Blog: Young Blog" +
            "Contact: young20050727@gmail.com" +
            "'信不信我用OSU電爆你'---Young 2021.08.23"

    @Test
    fun testRequests() {
        Main().main(arrayListOf())
        withTestApplication({ module(testing = true) }) {
            val boundary = "WebAppBoundary"
//            val fileBytes = File("a.jpg").readBytes()

            handleRequest(HttpMethod.Post, "/api/post") {

                addHeader(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )
                setBody(
                    boundary, listOf(
                        PartData.FormItem(
                            text, { }, headersOf(
                                HttpHeaders.ContentDisposition,
                                ContentDisposition.Inline
                                    .withParameter(ContentDisposition.Parameters.Name, "text")
                                    .toString()
                            )
                        ),
//                        PartData.FileItem({ fileBytes.inputStream().asInput() }, {}, headersOf(
//                            HttpHeaders.ContentDisposition,
//                            ContentDisposition.File
//                                .withParameter(ContentDisposition.Parameters.Name, "image")
//                                .withParameter(ContentDisposition.Parameters.FileName, "a.jpg")
//                                .toString()
//                        )
//                        )
                    )
                )
            }.apply {
                assertEquals("{\"error\":false,\"message\":\"ok\"}", response.content)
            }
        }
    }

    @Test
    fun testDraw() {
        defaultDraw(text, Date())
    }
}