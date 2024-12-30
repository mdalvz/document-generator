package dev.mdalvz.documentgeneratorlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.inject.Guice;
import com.google.inject.Inject;
import dev.mdalvz.documentgeneratorlambda.module.DocumentGeneratorLambdaModule;
import dev.mdalvz.documentgeneratorlambda.operation.Operation;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

public class DocumentGeneratorLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  Set<Operation> operations;

  public DocumentGeneratorLambda() {
    Guice.createInjector(new DocumentGeneratorLambdaModule())
        .injectMembers(this);
  }

  @Override
  public @NonNull APIGatewayProxyResponseEvent handleRequest(
      final @NonNull APIGatewayProxyRequestEvent event,
      final @NonNull Context context) {
    for (final Operation operation : operations) {
      if (operation.getHttpMethod().equals(event.getHttpMethod()) && operation.getPath().equals(event.getPath())) {
        return operation.handleRequest(event);
      }
    }
    return new APIGatewayProxyResponseEvent()
        .withIsBase64Encoded(false)
        .withStatusCode(404)
        .withHeaders(Map.of(
            "Content-Type",
            "text/plain"))
        .withBody("Cannot " + event.getHttpMethod() + " " + event.getPath());
  }

}
