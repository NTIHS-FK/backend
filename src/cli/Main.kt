package com.ntihs_fk.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.google.gson.Gson
import com.ntihs_fk.data.*
import com.ntihs_fk.functions.Config
import com.ntihs_fk.functions.init
import com.ntihs_fk.functions.initConfigFile
import com.ntihs_fk.functions.randomString
import com.ntihs_fk.module
import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.io.File

class Main : CliktCommand() {

    private val port: Int by option(help = "listening port").int().default(8080)
    private val httpsPort: Int by option(help = "listening port").int().default(8443)
    private val host: String by option(help = "host name").default("127.0.0.1")
    private val ssl by option(help = "ssl key store file path").file()
    private val twitterConfigFile by option(help = "twitter config file path").file()
        .default(File("./config/Twitter.config.json"))
    private val discordConfigFile by option(help = "discord config file path").file()
        .default(File("./config/Discord.config.json"))
    private val gmailConfigFile by option(help = "gmail config file path").file()
        .default(File("./config/Gmail.config.json"))
    private val googleConfigFile by option(help = "google config file path").file()
        .default(File("./config/Google.config.json"))
    private val adminConfigFile by option(help = "admin config file path").file()
        .default(File("./config/Admin.config.json"))

    override fun run() {
        val logger = LoggerFactory.getLogger("ntihs-fk.ktor.application")
        val gson = Gson()
        init(logger)

        initConfigFile(
            twitterConfigFile, TwitterConfigData(
                "you consumer key",
                "you consumer secret",
                "access token",
                "access token secret"
            )
        )

        initConfigFile(
            discordConfigFile, DiscordConfigData(
                "vote channel webhook link",
                "post channel webhook link",
                "discord bot id",
                "discord bot secret"
            )
        )

        initConfigFile(
            gmailConfigFile, GmailConfigData(
                "you email",
                "you password"
            )
        )

        initConfigFile(
            googleConfigFile, GoogleConfigData(
                "you google OAuth2 id",
                "you google OAuth2 secret"
            )
        )

        initConfigFile(
            adminConfigFile, AdminConfigData(
                randomString(30),
                randomString(10)
            ),
            false
        )

        // init config
        Config.discordConfig = gson.fromJson(discordConfigFile.readText(), DiscordConfigData::class.java)
        Config.gmailConfig = gson.fromJson(gmailConfigFile.readText(), GmailConfigData::class.java)
        Config.twitterConfig = gson.fromJson(twitterConfigFile.readText(), TwitterConfigData::class.java)
        Config.googleConfig = gson.fromJson(googleConfigFile.readText(), GoogleConfigData::class.java)
        Config.adminConfig = gson.fromJson(adminConfigFile.readText(), AdminConfigData::class.java)
        Config.port = port
        Config.domain = host
        Config.ssl = ssl != null

        // server config
        val environment = applicationEngineEnvironment {
            log = logger
            connector {
                port = Config.port as Int
            }

            if (ssl != null) {
                val keystore = generateCertificate(
                    file = ssl!!,
                    keyAlias = "sampleAlias",
                    keyPassword = "foobar",
                    jksPassword = "foobar"
                )

                sslConnector(
                    keyStore = keystore,
                    keyAlias = "sampleAlias",
                    keyStorePassword = { "foobar".toCharArray() },
                    privateKeyPassword = { "foobar".toCharArray() }) {
                    port = httpsPort
                    keyStorePath = ssl!!
                }
            }

            module(Application::module)
        }
        // start server
        embeddedServer(Netty, environment).start(wait = true)
    }
}