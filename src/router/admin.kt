package com.ntihs_fk.router

import io.ktor.auth.*
import io.ktor.routing.*

fun Route.admin() {
    authenticate("auth-jwt-admin") {
        route("/admin") {

        }
    }
}