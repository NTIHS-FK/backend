package com.ntihs_fk.functions

import at.favre.lib.crypto.bcrypt.BCrypt
import com.ntihs_fk.data.VerifyUserPasswordData
import com.ntihs_fk.database.UserTable
import com.ntihs_fk.error.UnauthorizedException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun verifyPassword(password: String, username: String): VerifyUserPasswordData {
    var userData: ResultRow? = null
    transaction {
        userData = UserTable.select {
            UserTable.name.eq(username).or(
                UserTable.email.eq(username)
            )
        }.firstOrNull()
    }

    if (userData == null) throw UnauthorizedException()

    val verify = BCrypt.verifyer()
        .verify(password.toCharArray(), userData!![UserTable.hashcode]).verified

    return VerifyUserPasswordData(verify, userData!!)
}