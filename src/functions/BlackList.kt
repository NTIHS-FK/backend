package com.ntihs_fk.functions

import com.ntihs_fk.database.JWTBlacklistTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class JWTBlacklist {

    fun addBlacklistTokenId(id: String, dateTime: Date) {
        transaction {
            JWTBlacklistTable.insert {
                it[this.id] = id
                it[this.dateTime] = dateTime.time
            }
        }
    }

    fun isInside(id: String): Boolean {
        var isInside = false

        transaction {
            isInside = JWTBlacklistTable.select {
                JWTBlacklistTable.id.eq(id)
            }.firstOrNull() == null
        }

        return isInside
    }
}

val jwtBlacklist = JWTBlacklist()