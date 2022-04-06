package no.nav.cache.cache

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@TypeDefs(
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
@Entity(name = "cache")
data class CacheEntryDAO(
    @Column(name = "nøkkel") @Id val nøkkel: String,
    @Column(name = "verdi") val verdi: String,
    @Column(name = "utløpsdato") val utløpsdato: ZonedDateTime,
    @Column(name = "opprettet") @CreatedDate val opprettet: ZonedDateTime? = null,
    @Column(name = "endret") @UpdateTimestamp val endret: ZonedDateTime? = null
)
