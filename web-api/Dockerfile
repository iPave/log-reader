FROM openjdk:8u151-jdk
ADD target/web-api-1.0.jar /web-api/web-api-1.0.jar
ADD target/classes/application.properties /web-api/application.properties
ADD start.sh /web-api/start.sh
RUN groupadd -r api && \
    useradd -r -g  api api && \
    chmod +x /web-api/start.sh && \
    chown -R api:api /web-api
USER api
WORKDIR /home/api
ENTRYPOINT /web-api/start.sh