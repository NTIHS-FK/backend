package com.ntihs_fk.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

fun isAdmin(name: String, password: String): String? {
    return if (
        name == Config.adminConfig.name &&
        password == Config.adminConfig.password
    ) {
        JWT.create()
            .withIssuer(Config.issuer)
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("username", name)
            .withClaim("avatar", "")
            .withClaim("verify", true)
            .withClaim("admin", true)
            .withClaim("type", "default")
            .withExpiresAt(
                Date(
                    System.currentTimeMillis() + 60000 * 60 * 2
                )
            )
            .sign(Algorithm.HMAC256(Config.secret))
    } else null
}