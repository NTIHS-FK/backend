package com.ntihs_fk.drawImage

import io.ktor.features.*

fun draw(mode: String): ((String) -> String) {
    val modes = mapOf(
        "default" to ::defaultDraw
    )
    return modes[mode] ?: run {
        throw BadRequestException("Not mode")
    }
}