package com.ntihs_fk.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.ntihs_fk.functions.Config
import com.ntihs_fk.module
import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

class Main : CliktCommand() {
    private val port: Int by option(help="listening port").int().default(8080)
    private val httpsPort: Int by option(help="listening port").int().default(8443)
    private val host: String by option(help="host name").default("127.0.0.1")
    private val ssl by option(help="ssl key store file path").file()

    override fun run() {
        val logger = LoggerFactory.getLogger("ktor.application")

        Config.port = port
        Config.domain = host
        Config.ssl = ssl != null

        val environment = applicationEngineEnvironment {
            log = logger
            connector {
                port = Config.port
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