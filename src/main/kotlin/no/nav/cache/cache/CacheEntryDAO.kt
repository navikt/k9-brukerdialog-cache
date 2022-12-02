package no.nav.cache.cache

import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.ZonedDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "cache")
data class CacheEntryDAO(
    @Column(name = "nøkkel") @Id val nøkkel: String,
    @Column(name = "verdi") val verdi: String,
    @Column(name = "utløpsdato") val utløpsdato: ZonedDateTime,
    @Column(name = "opprettet") @CreatedDate val opprettet: ZonedDateTime? = null,
    @Column(name = "endret") @UpdateTimestamp val endret: ZonedDateTime? = null
)
