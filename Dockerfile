FROM eclipse-temurin:17-jre-alpine

WORKDIR /build/libs/

ADD /build/libs/authorizer-1.0.jar .

ENV SPRING_PROFILE="dev"
ENV JAVA_OPTS="-Xms1024m -Xmx1024m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n"
COPY ./entrypoint.sh .

ENTRYPOINT ["java", "-jar" , "authorizer-1.0.jar"]