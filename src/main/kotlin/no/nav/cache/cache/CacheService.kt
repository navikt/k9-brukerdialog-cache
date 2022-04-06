package no.nav.cache.cache

import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

@Service
class CacheService(
    private val repo: CacheRepository,
    private val tokenValidationContextHolder: SpringTokenValidationContextHolder,
    @Value("\${krypto.passphrase}") private val kryptoPassphrase: String,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CacheService::class.java)
        private fun genererNøkkel(prefix: String, fnr: String) = "${prefix}_$fnr"
    }

    fun lagre(cacheEntryDTO: CacheRequestDTO): CacheResponseDTO {
        return repo.save(cacheEntryDTO.somCacheEntryDAO()).somCacheResponseDTO()
    }

    fun hentVerdi(nøkkelPrefiks: String): CacheResponseDTO? {
        val token = tokenValidationContextHolder.tokenValidationContext.firstValidToken.get()
        val fnr = token.personIdentifikator()
        return repo.findByNøkkel(genererNøkkel(nøkkelPrefiks, fnr))?.let {
            it.somCacheResponseDTO()
        }
    }

    private fun CacheEntryDAO.somCacheResponseDTO(): CacheResponseDTO {
        val token = tokenValidationContextHolder.tokenValidationContext.firstValidToken.get()
        val fnr = token.personIdentifikator()
        val krypto = Krypto(passphrase = kryptoPassphrase, fnr = fnr)
        return CacheResponseDTO(
            nøkkel = nøkkel,
            verdi = krypto.decrypt(verdi),
            utløpsdato = utløpsdato,
            opprettet = opprettet,
            endret = endret
        )
    }

    private fun CacheRequestDTO.somCacheEntryDAO(): CacheEntryDAO {
        val token = tokenValidationContextHolder.tokenValidationContext.firstValidToken.get()
        val fnr = token.personIdentifikator()
        val krypto = Krypto(passphrase = kryptoPassphrase, fnr = fnr)
        return CacheEntryDAO(
            nøkkel = genererNøkkel(nøkkelPrefiks, fnr),
            verdi = krypto.encrypt(verdi),
            utløpsdato = utløpsdato,
            opprettet = opprettet ?: ZonedDateTime.now(UTC),
            endret = endret
        )
    }
}

private fun JwtToken.personIdentifikator(): String =
    jwtTokenClaims["pid"] as String?
        ?: jwtTokenClaims["sub"] as String?
        ?: throw IllegalStateException("Token claims inneholder verken pid eller sub.")

