FROM alpine:3.7 AS builder



ENV JAVA_HOME=/opt/jdk \
    PATH=${PATH}:/opt/jdk/bin \
    LANG=C.UTF-8

RUN set -ex && \
    apk add --no-cache bash && \
    wget https://download.java.net/java/early_access/alpine/11/binaries/openjdk-11-ea+11_linux-x64-musl_bin.tar.gz -O jdk.tar.gz && \
    mkdir -p /opt/jdk && \
    tar zxvf jdk.tar.gz -C /opt/jdk --strip-components=1 && \
    rm jdk.tar.gz && \
    rm /opt/jdk/lib/src.zip

WORKDIR /app

COPY mlib mlib


RUN jlink --module-path mlib:$JAVA_HOME/jmods \
        --add-modules app --add-modules service.local  \
        --launcher run=app/com.app.Main \
        --output dist \
        --compress 2 \
        --strip-debug \
        --no-header-files \
        --no-man-pages

# Second stage: Copies the custom JRE into our image and runs it
FROM alpine:3.7


WORKDIR /app


COPY --from=builder /app/dist/ ./
COPY --from=builder /app/mlib/jeromq-0.4.3.jar  /app/mlib/service.proxy-1.0-SNAPSHOT.jar mlib/



ENTRYPOINT ["bin/run"]