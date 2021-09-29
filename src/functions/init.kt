package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.data.TwitterConfig
import java.io.File
import org.slf4j.Logger

fun init(log: Logger) {
    log.info("Init directory")

    val gson = Gson()
    val image = File("./image")
    val textImage = File("./textImage")
    val twitterConfigFIle = File("./Twitter.config.json")

    if (!image.exists() && !image.isDirectory)
        image.mkdir()
    if (!textImage.exists() && !textImage.isDirectory)
        textImage.mkdir()
    if (!twitterConfigFIle.exists() && !twitterConfigFIle.isFile)
        twitterConfigFIle.writeText(
            gson.toJson(
                TwitterConfig(
                    "",
                    "",
                    "",
                    ""
                )
            )
        )
}