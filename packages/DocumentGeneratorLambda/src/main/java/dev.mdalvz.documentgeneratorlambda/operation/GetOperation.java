package dev.mdalvz.documentgeneratorlambda.operation;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mdalvz.documentgeneratorcommon.dao.AuthorizationDAO;
import lombok.NonNull;

public abstract class GetOperation<TResponse> extends Operation {

  public GetOperation(final @NonNull ObjectMapper objectMapper,
                      final @NonNull AuthorizationDAO authorizationDAO,
                      final @NonNull String path) {
    super(objectMapper, authorizationDAO, "GET", path);
  }

  @Override
  protected @NonNull APIGatewayProxyResponseEvent handleAuthorizedRequest(
      final @NonNull APIGatewayProxyRequestEvent event) {
    try {
      return makeJSONResponse(200, handleGet(event));
    } catch (Exception e) {
      return makeErrorResponse(500, e);
    }
  }

  protected abstract @NonNull TResponse handleGet(
      final @NonNull APIGatewayProxyRequestEvent event);

}
