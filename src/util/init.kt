package com.ntihs_fk.functions

import java.io.File
import org.slf4j.Logger

fun init(log: Logger) {
    log.info("Init directory")

    val image = File("./image")
    val textImage = File("./textImage")
    val config = File("./config")

    if (!image.exists() && !image.isDirectory)
        image.mkdir()

    if (!textImage.exists() && !textImage.isDirectory)
        textImage.mkdir()

    if (!config.exists() && !config.isDirectory)
        config.mkdir()
}