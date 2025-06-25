FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /src/wiwokdetok

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true

COPY . .
RUN ./gradlew clean bootJar --no-daemon

FROM docker.io/library/eclipse-temurin:21-jre-alpine AS runner

ARG USER_NAME=wiwokdetok
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g ${USER_GID} ${USER_NAME} \
    && adduser -h /opt/wiwokdetok -D -u ${USER_UID} -G ${USER_NAME} ${USER_NAME}

USER ${USER_NAME}
WORKDIR /opt/wiwokdetok

COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/wiwokdetok/build/libs/*.jar wiwokdetok.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "wiwokdetok.jar"]
