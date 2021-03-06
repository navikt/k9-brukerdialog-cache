package no.nav.cache.cache

import no.nav.cache.util.TokenUtils.personIdentifikator
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
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
        val fnr = tokenValidationContextHolder.personIdentifikator()
        if (repo.existsById(genererNøkkel(cacheEntryDTO.nøkkelPrefiks, fnr)))
            throw CacheConflictException(cacheEntryDTO.nøkkelPrefiks)

        return repo.save(cacheEntryDTO.somCacheEntryDAO(fnr)).somCacheResponseDTO(fnr)
    }

    fun oppdater(cacheEntryDTO: CacheRequestDTO): CacheResponseDTO {
        val fnr = tokenValidationContextHolder.personIdentifikator()
        hent(cacheEntryDTO.nøkkelPrefiks)
        return repo.save(cacheEntryDTO.somCacheEntryDAO(fnr)).somCacheResponseDTO(fnr)
    }

    @Throws(CacheNotFoundException::class)
    fun hent(nøkkelPrefiks: String): CacheResponseDTO {
        val fnr = tokenValidationContextHolder.personIdentifikator()
        return repo.findByNøkkel(genererNøkkel(nøkkelPrefiks, fnr))?.let {
            it.somCacheResponseDTO(fnr)
        } ?: throw CacheNotFoundException(nøkkelPrefiks)
    }

    @Throws(FailedCacheDeletionException::class)
    fun slett(nøkkelPrefiks: String) {
        val verdi = hent(nøkkelPrefiks)
        repo.deleteById(verdi.nøkkel)
        if (repo.existsById(verdi.nøkkel)) throw FailedCacheDeletionException(nøkkelPrefiks)
    }

    @Scheduled(fixedRateString = "#{'\${no.nav.scheduled.utgått-cache}'}")
    fun slettUtgåtteCache() {
        logger.info("Sletter utgåtte cache...")
        val antallSlettedeCache = repo.deleteAllByUtløpsdatoIsBefore(ZonedDateTime.now(UTC))
        logger.info("Slettet {} utgåtte cache.", antallSlettedeCache)
    }

    private fun CacheEntryDAO.somCacheResponseDTO(fnr: String): CacheResponseDTO {
        val krypto = Krypto(passphrase = kryptoPassphrase, fnr = fnr)
        return CacheResponseDTO(
            nøkkel = nøkkel,
            verdi = krypto.decrypt(verdi),
            utløpsdato = utløpsdato,
            opprettet = opprettet,
            endret = endret
        )
    }

    private fun CacheRequestDTO.somCacheEntryDAO(fnr: String): CacheEntryDAO {
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

class CacheNotFoundException(nøkkelPrefiks: String) :
    RuntimeException("Cache med nøkkelPrefiks = $nøkkelPrefiks for person ble ikke funnet.")

class FailedCacheDeletionException(nøkkelPrefiks: String) :
    RuntimeException("Feilet med å slette cache med nøkkelPrefiks = $nøkkelPrefiks for person.")

class CacheConflictException(nøkkelPrefiks: String) :
    RuntimeException("Cache med nøkkelPrefiks = $nøkkelPrefiks finnes allerede for person.")

