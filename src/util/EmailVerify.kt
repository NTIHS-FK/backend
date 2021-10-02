package com.ntihs_fk.util

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
        .withDebugLogging(false)
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
        val emailData = EmailBuilder.startingBlank()
            .to(email)
            .from(Config.gmailConfig.email)
            .withSubject("靠北南工Email驗證信")
            .withPlainText("${Config.issuer}/email-verify?code=$token")
            .buildEmail()
        mailer.sendMail(emailData)
    }
}

val emailVerifyFun = EmailVerify()