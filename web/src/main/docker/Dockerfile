FROM openjdk:9
VOLUME /tmp
ADD web-1.0.0.BUILD-SNAPSHOT.jar /app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]