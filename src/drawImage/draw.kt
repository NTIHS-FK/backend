package com.ntihs_fk.drawImage

import io.ktor.features.*
import java.util.*

fun draw(mode: String): ((String, Date) -> ByteArray) {
    val modes = mapOf(
        "default" to ::defaultDraw
    )

    return modes[mode] ?: run {
        throw BadRequestException("Not mode")
    }
}