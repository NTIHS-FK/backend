package com.ntihs_fk.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import com.ntihs_fk.data.GmailConfig
import com.ntihs_fk.data.TwitterConfig
import com.ntihs_fk.functions.Config
import com.ntihs_fk.functions.initConfigFile
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
    private val twitterConfigFIle by option(help = "twitter config file path").file()
        .default(File("./Twitter.config.json"))
    private val discordConfigFIle by option(help = "discord config file path").file()
        .default(File("./Discord.config.json"))
    private val gmailConfigFile by option(help = "gmail config file path").file()
        .default(File("./Gmail.config.json"))

    override fun run() {
        val logger = LoggerFactory.getLogger("ktor.application")

        initConfigFile(
            twitterConfigFIle, TwitterConfig(
                "you consumer key",
                "you consumer secret",
                "access token",
                "access token secret"
            )
        )

        initConfigFile(
            discordConfigFIle, DiscordConfig(
                "vote channel webhook link",
                "post channel webhook link",
                "discord bot id",
                "discord bot secret"
            )
        )

        initConfigFile(
            gmailConfigFile, GmailConfig(
                "you email",
                "you password"
            )
        )

        // init config
        Config.port = port
        Config.domain = host
        Config.ssl = ssl != null
        Config.twitterConfigFile = twitterConfigFIle
        Config.discordConfigFile = discordConfigFIle
        Config.gmailConfigFile = gmailConfigFile

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

        embeddedServer(Netty, environment).start(wait = true)
    }
}