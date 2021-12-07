package com.ntihs_fk.router

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.static() {
    static {
        default("")
        files("./image")
    }
}