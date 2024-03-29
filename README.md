# K9-brukerdialog-cache

![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=ncloc)
![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=alert_status)
![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=coverage)
![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=code_smells)
![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=sqale_index)
![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=duplicated_lines_density)
![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_k9-brukerdialog-cache&metric=bugs)

![CI / CD](https://github.com/navikt/k9-brukerdialog-cache/workflows/CI%20/%20CD/badge.svg)
![Alerts](https://github.com/navikt/k9-brukerdialog-cache/workflows/Alerts/badge.svg)
![Vulnerabilities scanning of dependencies](https://github.com/navikt/k9-brukerdialog-cache/workflows/Vulnerabilities%20scanning%20of%20dependencies/badge.svg)

# Innholdsoversikt
* [1. Kontekst](#1-kontekst)
* [2. Funksjonelle Krav](#2-funksjonelle-krav)
* [3. Begrensninger](#3-begrensninger)
* [4. Programvarearkitektur](#5-programvarearkitektur)
* [5. Kode](#6-kode)
* [6. Data](#7-data)
* [7. Infrastrukturarkitektur](#8-infrastrukturarkitektur)
* [8. Distribusjon av tjenesten (deployment)](#9-distribusjon-av-tjenesten-deployment)
* [9. Utviklingsmiljø](#10-utviklingsmilj)
* [10. Drift og støtte](#11-drift-og-sttte)

# 1. Kontekst
Persistererinng av kortlevde (midlertidige) datastrukturer for tjenester i team brukerdialog.

# 2. Funksjonelle Krav
Denne tjenesten understøtter behovet for mellomlagring av datastrukturer, deriblant søknader.
Tjenesten eksponerer api-er for lagring, oppdatering og sletting av datastrukturerer i databasen.

# 3. Begrensninger

# 4. Programvarearkitektur

# 5. Kode

# 6. Data

# 7. Infrastrukturarkitektur

# 8. Distribusjon av tjenesten (deployment)
Distribusjon av tjenesten er gjort med bruk av Github Actions.
[Sif Innsyn API CI / CD](https://github.com/navikt/k9-brukerdialog-cache/actions)

Push/merge til master branche vil teste, bygge og deploye til produksjonsmiljø og testmiljø.

# 9. Utviklingsmiljø
## Forutsetninger
* docker
* docker-compose
* Java 17
* Kubectl

## Bygge Prosjekt
For å bygge kode, kjør:

```shell script
./gradlew clean build
```

## Kjøre Prosjekt
For å kjøre kode, kjør:

```shell script
./gradlew clean build && docker build --tag k9-brukerdialog-cache-local . && docker-compose up --build
```

Eller for å hoppe over tester under bygging:
```shell script
./gradlew clean build -x test && docker build --tag k9-brukerdialog-cache-local . && docker-compose up --build
```

### Henting av data via api-endepunktene
Applikasjonen er konfigurert med en lokal oicd provider stub for å utsending og verifisering av tokens. For å kunne gjøre kall på endepunktene, må man ha et gyldig token.

#### Henting av token
1. Åpne oicd-provider-gui i nettleseren enten ved å bruke docker dashbord, eller ved å gå til http://localhost:5000.
2. Trykk "Token for nivå 4" for å logge inn med ønsket bruker, ved å oppgi fødselsnummer. Tokenet blir da satt som en cookie (selvbetjening-idtoken) i nettleseren.
3. Deretter kan du åpne http://localhost:8080/swagger-ui.html for å teste ut endepunktene.

Om man ønsker å bruke postman må man selv, lage en cookie og sette tokenet manuelt. Eksempel:
selvbetjening-idtoken=eyJhbGciOiJSUzI1NiIsInR5cCI6Ikp.eyJzdWIiOiIwMTAxMDExMjM0NSIsImFjc.FBmVFuHI9d8akrVdAxi1dRg03qKV4EGk; Path=/; Domain=localhost; Expires=Fri, 18 Jun 2021 08:46:13 GMT;

# 10. Drift og støtte
## Logging
Loggene til tjenesten kan leses på to måter:

### Kibana
For [dev-gcp: ](TODO: fyll ut)

For [prod-gcp:](TODO: fyll ut)

### Kubectl
For dev-gcp:
```shell script
kubectl config use-context dev-gcp
kubectl get pods -n dusseldorf | grep k9-brukerdialog-cache
kubectl logs -f k9-brukerdialog-cache-<POD-ID> --namespace dusseldorf -c k9-brukerdialog-cache
```

For prod-gcp:
```shell script
kubectl config use-context prod-gcp
kubectl get pods -n dusseldorf | grep k9-brukerdialog-cache
kubectl logs -f k9-brukerdialog-cache-<POD-ID> --namespace dusseldorf -c k9-brukerdialog-cache
```

## Alarmer
Vi bruker [nais-alerts](https://doc.nais.io/observability/alerts) for å sette opp alarmer. Disse finner man konfigurert i [nais/alerterator-prod.yml](nais/alerterator-prod.yml).

## Metrics

## Henvendelser
Spørsmål koden eller prosjekttet kan rettes til team dusseldorf på:
* [\#sif-brukerdialog](https://nav-it.slack.com/archives/CQ7QKSHJR)

