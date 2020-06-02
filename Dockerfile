FROM maven:3-jdk-11 as build
WORKDIR /app

ARG GITHUB_USER
ARG GITHUB_TOKEN
ENV GITHUB_USER=$GITHUB_USER GITHUB_TOKEN=$GITHUB_TOKEN
COPY src src
COPY settings.xml /app/
RUN mvn -B -Dmaven.repo.local=/app/.m2 package -DskipTests --settings settings.xml

FROM openjdk:11-jre-slim
WORKDIR /app
VOLUME /tmp
COPY run.sh /app
RUN chmod +x run.sh
ENTRYPOINT [ "/app/run.sh" ]
EXPOSE 8083

COPY --from=build /app/target/ems-test-harness.war /app
