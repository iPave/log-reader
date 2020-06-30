FROM openjdk:8u151-jdk
ADD target/console-app-1.0-jar-with-dependencies.jar /console-app/console-app-1.0-jar-with-dependencies.jar
ADD target/classes/application.properties /console-app/application.properties
ADD start.sh /console-app/start.sh
RUN groupadd -r console-app && \
    useradd -r -g  console-app console-app && \
    chmod +x /console-app/start.sh && \
    chown -R console-app:console-app /console-app
USER console-app
ENTRYPOINT /console-app/start.sh