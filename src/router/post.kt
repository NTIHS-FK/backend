package com.ntihs_fk.router

import com.ntihs_fk.data.ArticleData
import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.drawImage.draw
import com.ntihs_fk.socialSoftware.discord.discordPost
import com.ntihs_fk.util.Config
import com.ntihs_fk.util.apiFrameworkFun
import com.ntihs_fk.util.randomString
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.apache.tika.Tika
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

fun Route.post(testing: Boolean) {


    post("/api/post") {
        val article = call.receiveMultipart()
        var fileName: String? = null
        var text: String? = null
        var textImageType = "default"

        article.forEachPart { part ->
            when (part) {

                // upload image file
                is PartData.FileItem -> {
                    if (fileName != null) throw BadRequestException("Multipart error")
                    val fileBytes = part.streamProvider().readBytes()
                    val fileType = Tika().detect(fileBytes)

                    fileName = Date().time.toString() + randomString(30) + part.originalFileName as String

                    call.application.log.info(fileType)

                    if (fileType.startsWith("image")) {
                        if (!testing)
                            File("./image/$fileName").writeBytes(fileBytes)
                    } else throw BadRequestException("This file not image")
                }

                // 貼文內容
                is PartData.FormItem -> {
                    when (part.name) {
                        "text" -> text = part.value
                        "textImageType" -> textImageType = part.value
                    }
                }
                else -> throw BadRequestException("Multipart error")
            }
        }

        if (text == null) throw BadRequestException("Missing text")
        else {
            // select text
            transaction {
                if (
                    ArticleTable.select {
                        ArticleTable.text.eq(text!!)
                    }.firstOrNull() != null
                ) throw BadRequestException("Duplicate publication")
            }

            // draw image
            val drawImageFileName = draw(textImageType)(text!!)

            // database
            if (!testing)
                transaction {
                    val data = ArticleTable.insert {
                        it[this.text] = text!!
                        it[this.image] = fileName
                        it[this.textImage] = drawImageFileName
                    }.resultedValues ?: throw Error("Insert error")

                    // post discord
                    if (!Config.discordConfig.disable)
                        for (i in data) {
                            discordPost(
                                Config.discordConfig.voteChannelWebhook,
                                i[ArticleTable.text],
                                i[ArticleTable.textImage],
                                i[ArticleTable.id]
                            )
                        }
                }

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

    get("/api/posts") {
        val page = call.request.queryParameters["page"]?.toInt() ?: 0
        val rePots = mutableListOf<ArticleData>()

        transaction {

            val data = ArticleTable.select {
                ArticleTable.voting.eq(true)
            }.limit(page * 10, (page + 1) * 10)

            for (i in data) {
                rePots.add(
                    ArticleData(
                        i[ArticleTable.id],
                        i[ArticleTable.time].millis,
                        null,
                        null,
                        i[ArticleTable.textImage],
                        i[ArticleTable.voting]
                    )
                )
            }
        }
        call.respond(apiFrameworkFun(rePots))
    }

    get("/post/{id}") {

        val id = call.parameters["id"]?.toInt() ?: throw BadRequestException("Missing parameter")
        var data: ResultRow? = null

        transaction {
            data = ArticleTable.select {
                ArticleTable.id.eq(id)
            }.firstOrNull()
        }

        if (data == null) throw NotFoundException()
        else
            call.respondHtml {
                head {
                    meta("og:title", "靠北南工")
                    meta("og:site_name", "靠北南工")
                    meta("og:description", data!![ArticleTable.text])
                    meta(
                        "og:image",
                        "${Config.issuer}/image/${data!![ArticleTable.textImage]}.jpg"
                    )
                    meta("og:url", "${Config.issuer}/post/$id")
                    meta("og:type", "website")
                    link {
                        href = ""
                    }
                    script {
                        src = ""
                    }
                }
                body {
                    div {
                        this.id = "root"
                    }
                }
            }
    }

    get("/api/post/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw BadRequestException("Missing parameter")
        var articleData: ResultRow? = null

        transaction {
            articleData = ArticleTable.select {
                ArticleTable.id.eq(id)
            }.firstOrNull() ?: throw BadRequestException("Not id")
        }

        if (articleData == null) throw BadRequestException("error")

        call.respond(
            apiFrameworkFun(
                ArticleData(
                    articleData!![ArticleTable.id],
                    articleData!![ArticleTable.time].millis,
                    articleData!![ArticleTable.text],
                    articleData!![ArticleTable.image],
                    articleData!![ArticleTable.textImage],
                    articleData!![ArticleTable.voting]
                )
            )
        )
    }
}