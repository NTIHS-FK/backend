package com.ntihs_fk.socialSoftware

import com.ntihs_fk.functions.Config
import com.ntihs_fk.socialSoftware.discord.discordPost
import socialSoftware.twitter.postTweet

fun mainPost(text: String, image: String? = null, textImage: String, id: Int) {
    val publishText = """
        |#靠北南工$id
        |$text
        |
    """.trimMargin()

    // social software

    discordPost(Config.discordConfig.postChannelWebhook, publishText, textImage, id)
    postTweet(publishText, image, textImage)
}