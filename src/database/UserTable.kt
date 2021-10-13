package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table

object UserTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = text("name")
    val email = text("email")
    val hashcode = text("hashcode")
    val verify = bool("verify").default(false)
    val admin = bool("admin").default(false)
}