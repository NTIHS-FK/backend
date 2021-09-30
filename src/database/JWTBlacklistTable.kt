package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table

object JWTBlacklistTable: Table() {
    val id = text("text").primaryKey()
    val dateTime = long("datetime")
}