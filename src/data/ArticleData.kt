package com.ntihs_fk.data

data class ArticleData(
    val id: Int,
    val time: Long,
    val text: String?,
    val image: String?,
    val textImage: String,
    val voting: Boolean
)