package no.nav.cache.cache

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.ZonedDateTime

@Entity(name = "cache")
data class CacheEntryDAO(
    @Column(name = "nøkkel") @Id val nøkkel: String,
    @Column(name = "verdi") val verdi: String,
    @Column(name = "ytelse") @Enumerated(EnumType.STRING) val ytelse: Ytelse? = null,
    @Column(name = "utkast_id") val utkastId: String? = null,
    @Column(name = "utløpsdato") val utløpsdato: ZonedDateTime,
    @Column(name = "opprettet") @CreatedDate val opprettet: ZonedDateTime? = null,
    @Column(name = "endret") @UpdateTimestamp val endret: ZonedDateTime? = null
)
