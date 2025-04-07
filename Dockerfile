FROM openjdk:21-jdk-slim

WORKDIR /hrm_app
VOLUME /opt/logs

RUN mkdir "/opt/logs"

ARG EXTRACTED_DIR=target/extracted
ARG PROFILE=prod
ARG ENV_FILE_PATH=config/docker/.env
ARG FLYWAY_PROPERTIES_PATH=config/flyway.properties

ENV EXTRACTED_DIR=${EXTRACTED_DIR}
ENV PROFILE=${PROFILE}
ENV ENV_FILE_PATH=${ENV_FILE_PATH}
ENV FLYWAY_PROPERTIES_PATH=${FLYWAY_PROPERTIES_PATH}

ENV TZ=Asia/Taipei
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -fs /usr/share/zoneinfo/$TZ /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

RUN apt-get install -y fontconfig

RUN echo "構建參數 EXTRACTED_DIR=${EXTRACTED_DIR}"
RUN echo "構建參數 PROFILE=${PROFILE}"
RUN echo "構建參數 ENV_FILE_PATH=${ENV_FILE_PATH}"
RUN echo "構建參數 FLYWAY_PROPERTIES_PATH=${FLYWAY_PROPERTIES_PATH}"

COPY ${EXTRACTED_DIR}/dependencies/ ./
COPY ${EXTRACTED_DIR}/spring-boot-loader/ ./
COPY ${EXTRACTED_DIR}/snapshot-dependencies/ ./
COPY ${EXTRACTED_DIR}/application/ ./
COPY ${ENV_FILE_PATH} ./config/.env
COPY ${FLYWAY_PROPERTIES_PATH} ./config/flyway.properties

#ENTRYPOINT ["java", "-DactiveProfile=${PROFILE}", "-DENV_FILE_PATH=config/.env", "-DFLYWAY_PROPERTIES_PATH=config/flyway.properties", "org.springframework.boot.loader.launch.JarLauncher"]
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-DENV_FILE_PATH=config/.env", "-DFLYWAY_PROPERTIES_PATH=config/flyway.properties", "org.springframework.boot.loader.launch.JarLauncher"]
