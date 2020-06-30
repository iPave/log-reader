FROM openjdk:8u151-jdk
ADD target/web-api-1.0.jar /web-api/web-api-1.0.jar
ADD target/classes/application.properties /web-api/application.properties
ADD start.sh /web-api/start.sh
RUN groupadd -r api && \
    useradd -r -g  api api && \
    chmod +x /web-api/start.sh && \
    chown -R api:api /web-api
WORKDIR /home/api
RUN chown -R api:api  /home/api
USER api

ENTRYPOINT /web-api/start.sh