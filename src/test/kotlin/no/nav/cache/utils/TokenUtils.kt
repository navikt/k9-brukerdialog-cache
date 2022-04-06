package no.nav.cache.utils

import com.nimbusds.jwt.SignedJWT
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

fun MockOAuth2Server.hentToken(
    subject: String = "12345678910",
    issuerId: String = "tokenx",
    claims: Map<String, String> = mapOf("acr" to "Level4"),
    audience: String = "aud-localhost",
    expiry: Long = 3600
): SignedJWT = issueToken(issuerId = issuerId, subject = subject, claims = claims, audience = audience, expiry = expiry)

fun SignedJWT.tokenTilHeader(): HttpHeaders {
    val token = serialize()
    val headers = HttpHeaders()
    headers.setBearerAuth(token)
    return headers
}
