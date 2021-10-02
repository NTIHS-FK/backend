package com.ntihs_fk.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.ntihs_fk.functions.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger

/**
 * 初始化資料庫
 * 傳入: [log]
 */
fun initDatabase(log: Logger) {
    log.info("Init Database")
    val config = HikariConfig("/hikari.properties")
    config.schema = "public"
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    // init table
    transaction {
        SchemaUtils.create(ArticleTable, UserTable, DiscordOAuth2Table, VoteTable, JWTBlacklistTable)
        // 我知道這寫法很破
        if (
            UserTable.select {
                UserTable.name eq Config.adminConfig.name
            }.firstOrNull() == null
        )
            UserTable.insert {
                it[name] = Config.adminConfig.name
                it[email] = ""
                it[hashcode] = BCrypt.withDefaults().hashToString(12, Config.adminConfig.password.toCharArray())
                it[verify] = true
            }
    }
}