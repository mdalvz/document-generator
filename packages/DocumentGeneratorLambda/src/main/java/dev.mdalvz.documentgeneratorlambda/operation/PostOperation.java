package dev.mdalvz.documentgeneratorlambda.operation;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mdalvz.documentgeneratorcommon.dao.AuthorizationDAO;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class PostOperation<TRequest, TResponse> extends Operation {

  private final @NonNull ObjectMapper objectMapper;

  private final @NonNull Class<TRequest> requestType;

  public PostOperation(final @NonNull ObjectMapper objectMapper,
                      final @NonNull AuthorizationDAO authorizationDAO,
                      final @NonNull String path,
                      final @NonNull Class<TRequest> requestType) {
    super(objectMapper, authorizationDAO, "POST", path);
    this.objectMapper = objectMapper;
    this.requestType = requestType;
  }

  @Override
  protected @NonNull APIGatewayProxyResponseEvent handleAuthorizedRequest(
      final @NonNull APIGatewayProxyRequestEvent event) {
    final String rawBody = event.getIsBase64Encoded()
        ? new String(Base64.getDecoder().decode(event.getBody()), StandardCharsets.UTF_8)
        : event.getBody();
    TRequest body;
    try {
      body = objectMapper.readValue(rawBody, requestType);
    } catch (Exception e) {
      return makeErrorResponse(400, e);
    }
    try {
      return makeJSONResponse(200, handlePost(event, body));
    } catch (Exception e) {
      return makeErrorResponse(500, e);
    }
  }

  protected abstract @NonNull TResponse handlePost(
      final @NonNull APIGatewayProxyRequestEvent event,
      final @NonNull TRequest body);

}
