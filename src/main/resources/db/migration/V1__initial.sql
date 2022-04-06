CREATE TABLE Cache
(
    nøkkel     TEXT PRIMARY KEY NOT NULL,
    verdi      TEXT             NOT NULL,
    utløpsdato timestamp        NOT NULL,
    opprettet  timestamp        NOT NULL,
    endret     timestamp
)
