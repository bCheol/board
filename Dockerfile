FROM eclipse-temurin:17-jdk AS build-stage

WORKDIR /app

COPY gradlew settings.gradle build.gradle ./

COPY gradle ./gradle

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew build -x test --no-daemon

FROM tomcat:10.1.49-jdk17

COPY setenv.sh /usr/local/tomcat/bin/setenv.sh

COPY --from=build-stage /app/build/libs/board-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 80

CMD ["catalina.sh", "run"]