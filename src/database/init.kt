package com.ntihs_fk.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.ntihs_fk.util.Config
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
    }
}