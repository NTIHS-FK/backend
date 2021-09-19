package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table
import java.util.*
import org.joda.time.DateTime

object ArticleTable : Table() {
    val id = integer("ArticleId").autoIncrement().primaryKey()
    val time = datetime("time").clientDefault { DateTime.now() }
    val text = text("Text")
    val image = text("Image")
    val textImageType = text("TextImageType")
    val vote = bool("Vote").clientDefault { false }
}