FROM ubuntu:noble

RUN apt-get -y update && \
    apt-get -y upgrade && \
    apt-get -y install openjdk-21-jre fonts-liberation libcurl3-gnutls libcurl4 libgbm1 wget xdg-utils

COPY ./chrome /tmp/chrome

RUN cat $(find /tmp/chrome -name "google-chrome_amd64.deb.*.bin" | sort) > /tmp/chrome/google-chrome_amd64.deb

RUN dpkg -i /tmp/chrome/google-chrome_amd64.deb && \
    apt-get -y install -f && \
    cp /tmp/chrome/chromedriver /usr/bin/chromedriver && \
    rm -rf /tmp/chrome

WORKDIR /function

COPY ./build/libs/DocumentGeneratorRenderLambda-1.0.jar .

ENTRYPOINT [ "java", "-cp", "./*", "com.amazonaws.services.lambda.runtime.api.client.AWSLambda" ]

CMD [ "dev.mdalvz.documentgeneratorrenderlambda.DocumentGeneratorRenderLambda::handleRequest" ]
