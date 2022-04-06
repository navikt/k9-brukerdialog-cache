package no.nav.cache.cache

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.cache.cache.CacheController.Endpoints.CACHE_PATH
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.spring.ProtectedRestController
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ProtectedRestController(issuer = "tokenx")
@SecurityRequirement(name = "bearer-jwt", scopes = ["read", "write"])
class CacheController(
    private val cacheService: CacheService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CacheController::class.java)
    }

    object Endpoints {
        const val CACHE_PATH = "/api/cache"
    }

    @PostMapping(CACHE_PATH)
    @ResponseStatus(CREATED)
    fun lagreVerdi(@RequestBody cacheEntryDTO: CacheRequestDTO): CacheResponseDTO {
        logger.info("Oppretter ny cache entry...")
        val verdi = this.cacheService.lagre(cacheEntryDTO)
        logger.info("Cache entry opprettet.")
        return verdi
    }

    @GetMapping("$CACHE_PATH/{nokkel-prefiks}")
    @Protected
    @ResponseStatus(OK)
    fun hentVerdi(@PathVariable("nokkel-prefiks", required = true) nøkkel: String): ResponseEntity<CacheResponseDTO>? {
        val verdi = this.cacheService.hentVerdi(nøkkel)
        return if (verdi == null) ResponseEntity(NOT_FOUND)
        else ResponseEntity(verdi, OK)
    }
}
