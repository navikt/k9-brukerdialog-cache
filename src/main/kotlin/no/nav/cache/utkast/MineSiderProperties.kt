package no.nav.cache.utkast

import no.nav.cache.cache.Ytelse
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import java.util.*

@ConfigurationProperties(prefix = "no.nav.mine-sider")
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
) {
    fun byggUtkast(ident: String, ytelse: Ytelse): Utkast {
        val builder = Utkast.Builder()
            .utkastId(UUID.randomUUID().toString())
            .ident(ident)
            .origin("k9-brukerdialog-cache")

        when (ytelse) {
            Ytelse.PLEIEPENGER_SYKT_BARN -> builder
                .defaultTittel(psbSoknad.tittel)
                .link(psbSoknad.link)

            Ytelse.ENDRINGSMELDING_PLEIEPENGER_SYKT_BARN -> builder
                .defaultTittel(psbEndringsmelding.tittel)
                .link(psbEndringsmelding.link)

            Ytelse.PLEIEPENGER_LIVETS_SLUTTFASE -> builder
                .defaultTittel(pleiepengerLivetsSlutt.tittel)
                .link(pleiepengerLivetsSlutt.link)

            Ytelse.OMSORGSPENGER_UTVIDET_RETT -> builder
                .defaultTittel(omsorgspengerUtvidetRett.tittel)
                .link(omsorgspengerUtvidetRett.link)

            Ytelse.OMSORGSPENGER_MIDLERTIDIG_ALENE -> builder
                .defaultTittel(omsorgspengerMidlertidigAlene.tittel)
                .link(omsorgspengerMidlertidigAlene.link)

            Ytelse.OMSORGSPENGER_UTBETALING_ARBEIDSTAKER -> builder
                .defaultTittel(omsorgspengerUtbetalingArbeidstaker.tittel)
                .link(omsorgspengerUtbetalingArbeidstaker.link)

            Ytelse.OMSORGSDAGER_ALENEOMSORG -> builder
                .defaultTittel(omsorgsdagerAleneomsorg.tittel)
                .link(omsorgsdagerAleneomsorg.link)

            Ytelse.OMSORGSPENGER_UTBETALING_SNF -> builder
                .defaultTittel(omsorgspengerUtbetalingSnf.tittel)
                .link(omsorgspengerUtbetalingSnf.link)

            Ytelse.ETTERSENDING -> builder
                .defaultTittel(ettersending.tittel)
                .link(ettersending.link)

            Ytelse.ETTERSENDING_PLEIEPENGER_SYKT_BARN -> builder
                .defaultTittel(ettersendingPleiepengerSyktBarn.tittel)
                .link(ettersendingPleiepengerSyktBarn.link)

            Ytelse.ETTERSENDING_PLEIEPENGER_LIVETS_SLUTTFASE -> builder
                .defaultTittel(ettersendingPleiepengerLivetsSlutt.tittel)
                .link(ettersendingPleiepengerLivetsSlutt.link)

            Ytelse.ETTERSENDING_OMP -> builder
                .defaultTittel(ettersendingOmp.tittel)
                .link(ettersendingOmp.link)
        }

        return builder.create()
    }
}

data class UtkastProperties(
    val link: String,
    val tittel: String,
)
