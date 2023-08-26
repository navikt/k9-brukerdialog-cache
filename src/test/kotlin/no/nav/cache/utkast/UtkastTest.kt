package no.nav.cache.utkast

import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*

class UtkastTest {

    @Test
    fun `forvent riktig serialisering av delete request`() {
        val utkastId = UUID.randomUUID().toString()
        val delete = Utkast.Builder()
            .utkastId(utkastId)
            .delete()

        JSONAssert.assertEquals(
            """
              {
                 "@event_name": "deleted",
                 "utkastId": "$utkastId"
              }
            """.trimIndent(), delete.serializeToJson(), false
        )
    }

    @Test
    fun `forvent riktig serialisering av create request`() {
        val utkastId = UUID.randomUUID().toString()
        val ident = "12345678910"
        val link = "https://www.nav.no/familie/sykdom-i-familien/soknad/pleiepenger/soknad"
        val tittel = "Søknad om pleiepenger sykt barn"
        val create = Utkast.Builder()
            .utkastId(utkastId)
            .ident(ident)
            .link(link)
            .defaultTittel(tittel)
            .create()

        JSONAssert.assertEquals(
            """
              {
                 "@event_name": "created",
                 "utkastId": "$utkastId",
                 "ident": "$ident",
                 "tittel": "$tittel",
                 "link": "$link"
              }
            """.trimIndent(), create.serializeToJson(), false
        )
    }
}


