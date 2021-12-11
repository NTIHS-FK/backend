package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table

object PrivateClaims: Table() {
    val email = text("email")
    val token = text("token")
}