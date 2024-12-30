FROM ubuntu:noble

RUN apt-get -y update && \
    apt-get -y upgrade && \
    apt-get -y install openjdk-21-jre

WORKDIR /function

COPY ./build/libs/DocumentGeneratorRenderLambda-1.0.jar .

ENTRYPOINT [ "java", "-cp", "./*", "com.amazonaws.services.lambda.runtime.api.client.AWSLambda" ]

CMD [ "dev.mdalvz.documentgeneratorrenderlambda.DocumentGeneratorRenderLambda::handleRequest" ]
