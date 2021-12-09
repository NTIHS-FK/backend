package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object ArticleTable : Table() {
    val id = integer("ArticleId").autoIncrement().primaryKey()
    val time = datetime("time").clientDefault { DateTime.now() }
    val text = text("Text")
    val image = text("Image").nullable()
    val imageType = text("imageType").clientDefault { "default" }
    val voting = bool("Vote").clientDefault { false }
}