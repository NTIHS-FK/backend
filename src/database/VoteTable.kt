package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table

object VoteTable : Table() {
    val id = integer("Id").autoIncrement().primaryKey()
    val name = text("name")
    val postId = integer("postId")
    val vote = bool("vote").clientDefault { false }
}