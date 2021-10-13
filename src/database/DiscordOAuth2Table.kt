package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table

object DiscordOAuth2Table : Table() {
    val id = long("id")
    val email = text("email")
}