package com.ntihs_fk.functions

class JWTBlacklist {
    private val blacklist: MutableList<String> = mutableListOf()

    fun addBlacklistTokenId(id: String) {
        blacklist.add(id)
    }

    fun isInside(id: String): Boolean {
        return id !in blacklist
    }
}

val jwtBlacklist = JWTBlacklist()