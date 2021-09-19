package com.ntihs_fk.functions

import kotlin.random.Random


fun randomString(): String {
    val chars = ('0'..'9') + ('a'..'z') + ('A'..'Z')
    return (0..30).map {
        chars[Random.nextInt(0, chars.size)]
    }.joinToString("")
}