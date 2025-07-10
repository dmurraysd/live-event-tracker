FROM wiremock/wiremock:3.12.1
COPY wiremock /home/wiremock
ENTRYPOINT ["/docker-entrypoint.sh", "--global-response-templating", "--disable-gzip", "--verbose"]

FROM amazoncorretto:21
WORKDIR /app
ARG JAR_FILE=target/live-event-tracker-0.0.14.jar
COPY ${JAR_FILE} /app/live-event-tracker-0.0.14.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/live-event-tracker-0.0.14.jar"]