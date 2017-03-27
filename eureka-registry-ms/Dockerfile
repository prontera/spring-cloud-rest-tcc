FROM daocloud.io/library/java:openjdk-8-jre-alpine
MAINTAINER Chris <chiuchunkin@gmail.com>

VOLUME /tmp
ADD ./target/eureka-registry-ms-0.0.1-SNAPSHOT.jar /app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]

EXPOSE 8763