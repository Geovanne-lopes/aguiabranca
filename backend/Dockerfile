FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY backend/pom.xml ./pom.xml
RUN mvn -B -DskipTests dependency:go-offline

COPY backend/src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update \
	&& apt-get install -y --no-install-recommends curl \
	&& rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/target/aguiabranca-api-*.jar app.jar

EXPOSE 8080
USER 10001:10001

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
	CMD curl --fail --silent http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]