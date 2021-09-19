package com.ntihs_fk.router

import com.ntihs_fk.drawImage.defaultDraw
import com.ntihs_fk.drawImage.draw
import com.ntihs_fk.functions.apiFrameworkFun
import com.ntihs_fk.functions.randomString
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
        var contentImageType = "default"

        article.forEachPart { part ->
            when(part) {
                // upload image file
                is PartData.FileItem -> {
                    if (fileName != null) throw BadRequestException("Multipart error")
                    val fileBytes = part.streamProvider().readBytes()
                    val fileType = Tika().detect(fileBytes)

                    fileName = Date().time.toString() + randomString() + part.originalFileName as String

                    call.application.log.info(fileType)

                    if (fileType.startsWith("image")) {
                        if (!testing)
                            File("./img/$fileName").writeBytes(fileBytes)
                    }
                    else throw BadRequestException("This file not image")
                }
                // 貼文內容
                is PartData.FormItem -> {
                    when(part.name) {
                        "text" -> text = part.value
                        "contentImageType" -> contentImageType = part.value
                    }
                }
                else -> throw BadRequestException("Multipart error")
            }
        }
        if (text == null) throw BadRequestException("Missing text")
        else {
            // draw image
            val drawImageFileName = draw(contentImageType)(text!!)
            // database

            // log OAO
            call.application.log.info(
                "[${call.request.host()}] " +
                        "Say:\n\u001B[34m[Text]\u001b[0m \n\u001B[35m$text\u001B[0m\n" +
                        "\u001B[34m[Image]\u001B[0m ${fileName ?: "No image"} " +
                        "\u001B[34m[Text Image]\u001B[0m $drawImageFileName"
            )

            // respond api
            call.respond(apiFrameworkFun(null))
        }

    }
}