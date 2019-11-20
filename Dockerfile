FROM maven:3-jdk-11 as deps
WORKDIR /app

COPY pom.xml .
RUN mvn -B -Dmaven.repo.local=/app/.m2 dependency:go-offline

FROM deps as build

COPY src src
RUN mvn -B -Dmaven.repo.local=/app/.m2 package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
VOLUME /tmp
COPY run.sh /app
RUN chmod +x run.sh
ENTRYPOINT [ "/app/run.sh" ]
EXPOSE 8083

COPY --from=build /app/target/ems-test-harness.war /app
