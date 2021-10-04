package com.ntihs_fk.util

import kotlin.random.Random

fun randomString(count: Int): String {
    val chars = ('0'..'9') + ('a'..'z') + ('A'..'Z')
    return (0..count).map {
        chars[Random.nextInt(0, chars.size)]
    }.joinToString("")
}