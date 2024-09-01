FROM openjdk:17-slim
COPY config/AuthKey.p8 /app/config/AuthKey.p8
COPY ./build/libs/*T.jar app.jar
CMD ["java","-jar","-Dspring.profiles.active=${profiles}","app.jar"]
EXPOSE 8080
