package com.ntihs_fk.database

import org.jetbrains.exposed.sql.Table

object ArticleTable : Table() {
    val id = integer("ArticleId").autoIncrement().primaryKey()
    val text = integer("Text")
    val image = integer("Image")
    val contentImageType = integer("ContentImageType")
}