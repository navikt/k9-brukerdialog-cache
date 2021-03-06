package no.nav.cache.http

import no.nav.cache.cache.CacheConflictException
import no.nav.cache.cache.CacheNotFoundException
import no.nav.cache.cache.FailedCacheDeletionException
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.web.advice.AdviceTrait
import org.zalando.problem.spring.web.advice.ProblemHandling
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.Charset

@ControllerAdvice
class ExceptionHandler : ProblemHandling, AdviceTrait {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun håndtereGeneriskException(exception: Exception, request: ServletWebRequest): ResponseEntity<Problem> {
        log(HttpStatus.INTERNAL_SERVER_ERROR, exception, request)
        return create(Status.INTERNAL_SERVER_ERROR, exception, request, URI("/problem-details/internal-server-error"))
    }

    @ExceptionHandler(value = [CacheNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun håndterCacheIkkeFunnet(
        exception: CacheNotFoundException,
        request: ServletWebRequest
    ): ResponseEntity<Problem> {
        logger.trace("$exception")
        val throwableProblem = Problem.builder()
            .withType(URI("/problem-details/cache-ikke-funnet"))
            .withTitle("Cache ikke funnet")
            .withStatus(Status.NOT_FOUND)
            .withDetail(exception.message)
            .withInstance(URI(URLDecoder.decode(request.request.requestURL.toString(), Charset.defaultCharset())))
            .build()

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(throwableProblem)
    }

    @ExceptionHandler(value = [FailedCacheDeletionException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun håndterSlettingAvCacheFeil(
        exception: FailedCacheDeletionException,
        request: ServletWebRequest
    ): ResponseEntity<Problem> {

        val throwableProblem = Problem.builder()
            .withType(URI("/problem-details/sletting-av-cache-feilet"))
            .withTitle("Sletting av cache feilet")
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withDetail(exception.message)
            .withInstance(URI(URLDecoder.decode(request.request.requestURL.toString(), Charset.defaultCharset())))
            .build()

        return create(throwableProblem, request)
    }

    @ExceptionHandler(value = [CacheConflictException::class])
    @ResponseStatus(HttpStatus.CONFLICT)
    fun håndterCacheKonfliktFeil(
        exception: CacheConflictException,
        request: ServletWebRequest
    ): ResponseEntity<Problem> {

        val throwableProblem = Problem.builder()
            .withType(URI("/problem-details/cache-konflikt"))
            .withTitle("Cache med eksisterende nøkkel eksisterer allerede.")
            .withStatus(Status.CONFLICT)
            .withDetail(exception.message)
            .withInstance(URI(URLDecoder.decode(request.request.requestURL.toString(), Charset.defaultCharset())))
            .build()

        return create(throwableProblem, request)
    }

    @ExceptionHandler(value = [JwtTokenUnauthorizedException::class])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun håndtereTokenUnauthorizedException(
        exception: JwtTokenUnauthorizedException,
        request: ServletWebRequest
    ): ResponseEntity<Problem> {
        val throwableProblem = Problem.builder()
            .withType(URI("/problem-details/uautentisert-forespørsel"))
            .withTitle("Ikke autentisert")
            .withStatus(Status.UNAUTHORIZED)
            .withDetail(exception.message)
            .withInstance(URI(URLDecoder.decode(request.request.requestURL.toString(), Charset.defaultCharset())))
            .build()

        return create(throwableProblem, request)
    }

    @ExceptionHandler(JwtTokenValidatorException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun håndtereTokenUnauthenticatedException(
        exception: JwtTokenValidatorException,
        request: ServletWebRequest
    ): ResponseEntity<Problem> {
        log(HttpStatus.FORBIDDEN, exception, request)

        val throwableProblem = Problem.builder()
            .withType(URI("/problem-details/uautorisert-forespørsel"))
            .withTitle("Ikke uautorisert")
            .withStatus(Status.FORBIDDEN)
            .withDetail(exception.message)
            .withInstance(URI(URLDecoder.decode(request.request.requestURL.toString(), Charset.defaultCharset())))
            .build()

        return create(throwableProblem, request)
    }

    @ExceptionHandler(JwtTokenMissingException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun håndtereJwtTokenMissingException(
        exception: JwtTokenMissingException,
        request: ServletWebRequest
    ): ResponseEntity<Problem> {
        val throwableProblem = Problem.builder()
            .withType(URI("/problem-details/uautorisert-forespørsel"))
            .withTitle("Ikke uautorisert")
            .withStatus(Status.UNAUTHORIZED)
            .withDetail(exception.message)
            .withInstance(URI(URLDecoder.decode(request.request.requestURL.toString(), Charset.defaultCharset())))
            .build()

        return create(throwableProblem, request)
    }

    fun log(status: HttpStatus, exception: Exception, request: WebRequest) {
        logger.error("{} - {}, {}", status, exception, request.toString())
    }
}
