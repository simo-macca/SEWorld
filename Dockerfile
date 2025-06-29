FROM gradle:8.5-jdk21 AS builder
WORKDIR /app


COPY . /app

RUN gradle build -x test -x spotlessCheck

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]