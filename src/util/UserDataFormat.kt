package com.ntihs_fk.util

class UserDataFormat {
    companion object {

        fun isEmail(email: String) = Regex("^[A-Za-z](.*)([@])(.+)(\\.)(.+)").matches(email)
        fun isName(name: String) = Regex("[A-Za-z0-9_]").matches(name)
        fun isPassword(password: String) =
            Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$").matches(password)
    }
}