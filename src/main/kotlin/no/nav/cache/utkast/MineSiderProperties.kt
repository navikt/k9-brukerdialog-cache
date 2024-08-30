package no.nav.cache.utkast

import no.nav.cache.cache.Ytelse
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.net.URL
import java.util.*

@ConfigurationProperties(prefix = "no.nav.mine-sider")
@Validated
data class MineSiderProperties @ConstructorBinding constructor(
    val psbSoknad: UtkastProperties,
    val psbEndringsmelding: UtkastProperties,
    val pleiepengerLivetsSlutt: UtkastProperties,
    val omsorgspengerUtvidetRett: UtkastProperties,
    val omsorgspengerMidlertidigAlene: UtkastProperties,
    val omsorgspengerUtbetalingArbeidstaker: UtkastProperties,
    val omsorgsdagerAleneomsorg: UtkastProperties,
    val omsorgspengerUtbetalingSnf: UtkastProperties,
    val ettersending: UtkastProperties,
    val ettersendingPleiepengerSyktBarn: UtkastProperties,
    val ettersendingPleiepengerLivetsSlutt: UtkastProperties,
    val ettersendingOmp: UtkastProperties,
    val opplaringspenger: UtkastProperties
) {
    fun opprettUtkast(ident: String, ytelse: Ytelse): Utkast {
        val builder = Utkast.Builder()
            .utkastId(UUID.randomUUID().toString())
            .ident(ident)
            .origin("k9-brukerdialog-cache")

        when (ytelse) {
            Ytelse.PLEIEPENGER_SYKT_BARN -> builder
                .defaultTittel(psbSoknad.tittel)
                .link(psbSoknad.link.toString())

            Ytelse.ENDRINGSMELDING_PLEIEPENGER_SYKT_BARN -> builder
                .defaultTittel(psbEndringsmelding.tittel)
                .link(psbEndringsmelding.link.toString())

            Ytelse.PLEIEPENGER_LIVETS_SLUTTFASE -> builder
                .defaultTittel(pleiepengerLivetsSlutt.tittel)
                .link(pleiepengerLivetsSlutt.link.toString())

            Ytelse.OMSORGSPENGER_UTVIDET_RETT -> builder
                .defaultTittel(omsorgspengerUtvidetRett.tittel)
                .link(omsorgspengerUtvidetRett.link.toString())

            Ytelse.OMSORGSPENGER_MIDLERTIDIG_ALENE -> builder
                .defaultTittel(omsorgspengerMidlertidigAlene.tittel)
                .link(omsorgspengerMidlertidigAlene.link.toString())

            Ytelse.OMSORGSPENGER_UTBETALING_ARBEIDSTAKER -> builder
                .defaultTittel(omsorgspengerUtbetalingArbeidstaker.tittel)
                .link(omsorgspengerUtbetalingArbeidstaker.link.toString())

            Ytelse.OMSORGSDAGER_ALENEOMSORG -> builder
                .defaultTittel(omsorgsdagerAleneomsorg.tittel)
                .link(omsorgsdagerAleneomsorg.link.toString())

            Ytelse.OMSORGSPENGER_UTBETALING_SNF -> builder
                .defaultTittel(omsorgspengerUtbetalingSnf.tittel)
                .link(omsorgspengerUtbetalingSnf.link.toString())

            Ytelse.ETTERSENDING -> builder
                .defaultTittel(ettersending.tittel)
                .link(ettersending.link.toString())

            Ytelse.ETTERSENDING_PLEIEPENGER_SYKT_BARN -> builder
                .defaultTittel(ettersendingPleiepengerSyktBarn.tittel)
                .link(ettersendingPleiepengerSyktBarn.link.toString())

            Ytelse.ETTERSENDING_PLEIEPENGER_LIVETS_SLUTTFASE -> builder
                .defaultTittel(ettersendingPleiepengerLivetsSlutt.tittel)
                .link(ettersendingPleiepengerLivetsSlutt.link.toString())

            Ytelse.ETTERSENDING_OMP -> builder
                .defaultTittel(ettersendingOmp.tittel)
                .link(ettersendingOmp.link.toString())

            Ytelse.OPPLÆRINGSPENGER -> builder
                .defaultTittel(opplaringspenger.tittel)
                .link(opplaringspenger.link.toString())
        }

        return builder.create()
    }
}

data class UtkastProperties(
    val link: URL,
    val tittel: String,
)
