package com.ntihs_fk.util

import at.favre.lib.crypto.bcrypt.BCrypt
import com.ntihs_fk.data.VerifyUserPasswordData
import com.ntihs_fk.database.UserTable
import com.ntihs_fk.error.UnauthorizedRequestException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun verifyPassword(password: String, username: String): VerifyUserPasswordData {
    var userData: ResultRow? = null
    // 尋找有沒有這個人?
    transaction {
        userData = UserTable.select {
            UserTable.name.eq(username).or(
                UserTable.email.eq(username)
            )
        }.firstOrNull()
    }
    // 沒找到就給他丟錯誤
    if (userData == null) throw UnauthorizedRequestException()
    // 驗證密碼真實性
    val verify = BCrypt.verifyer()
        .verify(password.toCharArray(), userData!![UserTable.hashcode]).verified

    return VerifyUserPasswordData(verify, userData!!)
}