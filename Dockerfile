FROM java:8
MAINTAINER whatzhangy
EXPOSE 8080
RUN mkdir /app && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/shanghai" > /etc/timezone
RUN mkdir /app/config
WORKDIR /app
ADD /starter/target/starter-0.0.1-SNAPSHOT.jar app.jar
CMD java ${JAVA_OPTS} -jar app.jar
