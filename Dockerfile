FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /src/kapsulkeaslian

COPY gradlew .
COPY gradle ./gradle

COPY build.gradle.kts settings.gradle.kts ./

COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runner

ARG USER_NAME=kapsulkeaslian
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g ${USER_GID} ${USER_NAME} \
    && adduser -D -u ${USER_UID} -G ${USER_NAME} -h /opt/kapsulkeaslian ${USER_NAME}

USER ${USER_NAME}
WORKDIR /opt/kapsulkeaslian
COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/kapsulkeaslian/build/libs/*.jar kapsulkeaslian.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "kapsulkeaslian.jar"]
