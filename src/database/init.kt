package com.ntihs_fk.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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
        SchemaUtils.create(ArticleTable, UserTable, VoteTable, JWTBlacklistTable, PrivateClaims)
    }
}