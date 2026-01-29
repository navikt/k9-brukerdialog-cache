FROM ghcr.io/navikt/sif-baseimages/java-chainguard-21:2026.01.29.1038Z

COPY build/libs/*.jar app.jar

CMD [ "-jar", "app.jar"]
