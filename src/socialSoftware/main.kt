package com.ntihs_fk.socialSoftware

import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.functions.Config
import com.ntihs_fk.socialSoftware.discord.discordPost
import io.ktor.features.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import socialSoftware.twitter.postTweet

fun mainPost(id: Int) {
    lateinit var article: ResultRow

    transaction {
        article = ArticleTable.select {
            ArticleTable.id.eq(id)
        }.firstOrNull() ?: throw BadRequestException("There is no this article")
    }

    val publishText = """
        |#靠北南工$id
        |----------------------------
        |
        |${article[ArticleTable.text]}
        |
        |----------------------------
        |
        |Discord --->
        |Twitter --->
        |FaceBook --->
        |Instagram --->
    """.trimMargin()

    // social software

    discordPost(Config.discordConfig.postChannelWebhook, publishText, article[ArticleTable.textImage], id)
    postTweet(publishText, article[ArticleTable.image], article[ArticleTable.textImage])
}