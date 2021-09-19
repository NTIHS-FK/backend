package com.ntihs_fk.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.apache.tika.Tika
import java.io.File
import java.util.*

fun Route.post(testing: Boolean) {
    post("/api/post") {
        if (call.request.contentType().contentType != "multipart")
            throw BadRequestException("Error request")
        val article = call.receiveMultipart()
        var fileName: String?

        article.forEachPart { part ->
            when(part) {
                is PartData.FileItem -> {
                    fileName = Date().time.toString() + part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    val fileType = Tika().detect(fileBytes)
                    call.application.log.info(fileType)

                    if (fileType.startsWith("image")) {
                        if (!testing)
                            File("./img/$fileName").writeBytes(fileBytes)
                        call.respondText("ok")
                    }
                    else throw BadRequestException("This file not image")
                }
                is PartData.FormItem -> {
                    part.name
                }
                else -> throw BadRequestException("Multipart error")
            }
        }
    }
}