FROM alpine:latest

ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"

RUN set -x \
  && sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
  && apk update \
  && apk add --no-cache openjdk11 zip unzip openssl openssl-dev curl tzdata \
  && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
  && echo "Asia/Shanghai" > /etc/timezone \
  && apk del tzdata \
  && rm -rf /var/cache/apk/*

ENV PATH=$PATH:${JAVA_HOME}/bin

RUN mkdir -p /im
RUN mkdir -p /im/upload
RUN mkdir -p /im/logs

WORKDIR /im

EXPOSE 8080

ADD im.jar ./


CMD java -Xms1024m -Xmx2048m -Djava.security.egd=file:/dev/./urandom -jar im.jar


