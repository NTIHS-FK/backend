package com.ntihs_fk.data

data class User(val nameOrEmail: String?, val password: String?)
data class SignIn(val name: String?, val email: String?, val password: String?)