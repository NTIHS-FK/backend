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
        val article = call.receiveMultipart()
        val part = article.readPart() ?: throw BadRequestException("Fail")

        if (part is PartData.FileItem) {
            val fileName: String = Date().time.toString() + part.originalFileName as String
            val fileBytes = part.streamProvider().readBytes()
            val fileType = Tika().detect(fileBytes)
            call.application.log.info(fileType)

            if (fileType.startsWith("image")) {
                if (!testing)
                    File("./img/$fileName").writeBytes(fileBytes)
            }
            else throw BadRequestException("Fail")
        }
        call.respondText("ok", status = HttpStatusCode.OK).apply {
            call.application.log.info("aasd")
        }
    }
}