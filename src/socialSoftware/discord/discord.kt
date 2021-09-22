package com.ntihs_fk.socialSoftware.discord

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.ntihs_fk.functions.domain
import com.ntihs_fk.functions.ssl
import io.ktor.features.*
import io.ktor.http.*

// discord publish

fun discordPost(webhookUrl: String, text: String, textImage: String, id: Int) {

    val embed = DiscordWebhookEmbed(
        author = Author(
            name = "xiao xigua#8787",
            url = "https://github.com/xiaoxigua-1",
            icon_url = "https://cdn.discordapp.com/avatars/458988300418416640/05549a0ef3b6c2d804214f8c8e2c40bd.webp"
        ),
        color = 0xE8D44F,
        image = Image(
            url = "http${if (ssl) "s" else ""}://$domain/textImage/$textImage.jpg"
        ),
        description = text,
        footer = Footer(
            text = "靠北南工",
            icon_url = "https://cdn.discordapp.com/avatars/889564072088068197/195b80329d0cd6eb999fd6ee2f5b34f6.webp"
        ),
        title = "#靠北南工$id",
        url = "http${if (ssl) "s" else ""}://$domain/post/$id",
    )
    val json = Gson().toJson(DiscordWebhookData("靠北南工", embeds = listOf(embed)))
    if (
        !HttpRequest.post(webhookUrl)
            .contentType(ContentType.Application.Json.toString())
            .send(json)
            .ok()
    ) throw BadRequestException("Discord post error")
}