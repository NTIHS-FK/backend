package com.ntihs_fk.data

data class Login(val token: String)
data class LoginData(val nameOrEmail: String?, val password: String?)
data class SignIn(val name: String?, val email: String?, val password: String?)
data class UserData(val name: String, val avatar: String)