package com.ntihs_fk.functions

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import java.util.*

class EmailVerify {
    private val keyPair = Keys.keyPairFor(SignatureAlgorithm.RS512)
    private val mailer = MailerBuilder
        .withSMTPServer("smtp.gmail.com", 465, Config.gmailConfig.email, Config.gmailConfig.password)
        .withTransportStrategy(TransportStrategy.SMTPS)
        .withSessionTimeout(10 * 1000)
        .withDebugLogging(true)
        .buildMailer()

    private fun createJWSToken(email: String): String {
        return Jwts.builder()
            .setSubject("EmailVerify")
            .setIssuer(Config.issuer)
            .setExpiration(Date(System.currentTimeMillis() + Config.expiresAt))
            .setIssuedAt(Date())
            .setId(UUID.randomUUID().toString())
            .claim("email", email)
            .signWith(keyPair.private, SignatureAlgorithm.RS512)
            .compact()
    }

    fun parserJWSToken(token: String): String {

        val jws = Jwts.parserBuilder()
            .requireSubject("EmailVerify")
            .setSigningKey(keyPair.public)
            .build()
            .parseClaimsJws(token)

        return jws.body["email"].toString()
    }

    fun sendEmail(email: String) {
        val token = this.createJWSToken(email)
        val email = EmailBuilder.startingBlank()
            .to(email)
            .from(Config.gmailConfig.email)
            .withPlainText(token)
            .buildEmail()
        mailer.sendMail(email)
    }
}

val emailVerifyFun = EmailVerify()