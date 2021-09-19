package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table
import java.util.*
import org.joda.time.DateTime

object ArticleTable : Table() {
    val id = integer("ArticleId").autoIncrement().primaryKey()
    val data = date("time").clientDefault { DateTime.now() }
    val text = text("Text")
    val image = text("Image")
    val contentImageType = text("ContentImageType")
    val vote = bool("Vote").clientDefault { false }
}