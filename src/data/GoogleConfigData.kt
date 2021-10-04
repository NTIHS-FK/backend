package com.ntihs_fk.data

data class GoogleConfigData(
    val client_id: String,
    val client_secret: String,
    val auth_provider_x509_cert_url: String = "https://www.googleapis.com/oauth2/v1/certs",
    val token_uri: String = "https://oauth2.googleapis.com/token",
    val auth_uri: String = "https://accounts.google.com/o/oauth2/auth",
    val disable: Boolean = false
)
