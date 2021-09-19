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
        defaultDraw("想問大家有沒有遇見不斷勤說別人離職，但自己卻在公司待了很久的人？\n" +
                "\n" +
                "我明白這有可能是因為忌才而說的話，但有些能力平平，甚至是職務上完全威脅不到他的人也被他勸說，老實說我真的不明白。\n" +
                "\n" +
                "如果他是認為公司待遇差，那他應該自己先辭職吧？自己不辭還要三番四次纏上不同部門的人，什麼心態？")
    }
}
