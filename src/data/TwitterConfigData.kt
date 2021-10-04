package com.ntihs_fk.data

data class TwitterConfigData(
    val consumerKey: String,
    val consumerSecret: String,
    val accessToken: String,
    val accessTokenSecret: String,
    val disable: Boolean = false
)
