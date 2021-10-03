package com.ntihs_fk.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.google.gson.Gson
import com.ntihs_fk.data.*
import com.ntihs_fk.module
import com.ntihs_fk.util.Config
import com.ntihs_fk.util.init
import com.ntihs_fk.util.initConfigFile
import com.ntihs_fk.util.randomString
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
    private val configFile by option(help = "admin config file path").file()
        .default(File("./Config.json"))

    override fun run() {
        val logger = LoggerFactory.getLogger("ntihs-fk.ktor.application")
        val gson = Gson()

        init(logger)

        val configInitData = ConfigData(
            GoogleConfigData(
                "you google OAuth2 id",
                "you google OAuth2 secret"
            ),
            DiscordConfigData(
                "vote channel webhook link",
                "post channel webhook link",
                "discord bot id",
                "discord bot secret"
            ),
            AdminConfigData(
                randomString(30),
                randomString(10)
            ),
            TwitterConfigData(
                "you consumer key",
                "you consumer secret",
                "access token",
                "access token secret"
            ),
            GmailConfigData(
                "you email",
                "you password"
            )
        )

        initConfigFile(configFile, configInitData)

        val configData = gson.fromJson(configFile.readText(), ConfigData::class.java)

        // init config
        Config.discordConfig = configData.discord
        Config.gmailConfig = configData.gmail
        Config.twitterConfig = configData.twitter
        Config.googleConfig = configData.google
        Config.adminConfig = configData.admin
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