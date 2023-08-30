package no.nav.cache.utkast

import no.nav.cache.cache.Ytelse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UtkastService(
    private val mineSiderService: MineSiderService,
    private val mineSiderProperties: MineSiderProperties,
) {

    private companion object {
        private val logger = LoggerFactory.getLogger(UtkastService::class.java)
    }

    fun opprettUtkast(ident: String, ytelse: Ytelse): Utkast {
        val utkast = mineSiderProperties.opprettUtkast(ident, ytelse)
        logger.info("Oppretter utkast for ytelse: $ytelse med id: ${utkast.utkastId}")
        mineSiderService.sendUtkast(utkast.utkastId!!, utkast.byggK9Utkast(ytelse))
        return utkast
    }

    fun slettUtkast(ytelse: Ytelse, utkastId: String) {
        logger.info("Sletter utkast ytelse: $ytelse med id: $utkastId")
        val utkast = Utkast.Builder().utkastId(utkastId).delete()
        mineSiderService.sendUtkast(utkastId, utkast.byggK9Utkast(ytelse))
    }
}
