package com.ntihs_fk.router

import com.ntihs_fk.router.admin.adminPost
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.admin() {
    authenticate("auth-jwt-admin") {
        route("/admin") {
            adminPost()

            static {
                // 等寫完frontend再寫上去
                default("")
            }
        }
    }
}