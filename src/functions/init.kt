package com.ntihs_fk.functions

import java.io.File
import org.slf4j.Logger

fun init(log: Logger) {
    val image = File("./image")
    val textImage = File("./textImage")

    if (!image.exists()  && !image.isDirectory)
        image.mkdir()
    if (!textImage.exists() && !textImage.isDirectory)
        textImage.mkdir()
    log.info("Init directory")
}