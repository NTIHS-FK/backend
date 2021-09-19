package com.ntihs_fk.router

import com.ntihs_fk.functions.apiFrameworkFun
import io.ktor.application.*
import io.ktor.features.*
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
        var fileName: String? = null
        var text: String? = null
        var contentImageType: String? = "default"

        article.forEachPart { part ->
            when(part) {
                is PartData.FileItem -> {
                    if (fileName != null) throw BadRequestException("Multipart error")
                    val fileBytes = part.streamProvider().readBytes()
                    val fileType = Tika().detect(fileBytes)

                    fileName = Date().time.toString() + part.originalFileName as String

                    call.application.log.info(fileType)

                    if (fileType.startsWith("image")) {
                        if (!testing)
                            File("./img/$fileName").writeBytes(fileBytes)
                        call.respond(apiFrameworkFun(null))
                    }
                    else throw BadRequestException("This file not image")
                }
                is PartData.FormItem -> {
                    when(part.name) {
                        "text" -> text = part.value
                        "contentImageType" -> contentImageType = part.value
                    }
                }
                else -> throw BadRequestException("Multipart error")
            }
        }
    }
}