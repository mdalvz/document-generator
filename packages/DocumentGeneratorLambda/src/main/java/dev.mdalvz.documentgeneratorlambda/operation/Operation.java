package dev.mdalvz.documentgeneratorlambda.operation;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mdalvz.documentgeneratorcommon.dao.AuthorizationDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class Operation {

  private static final String BEARER_PREFIX = "Bearer ";

  private final @NonNull ObjectMapper objectMapper;

  private final @NonNull AuthorizationDAO authorizationDAO;

  private final @NonNull String httpMethod;

  private final @NonNull String path;

  public @NonNull APIGatewayProxyResponseEvent handleRequest(final @NonNull APIGatewayProxyRequestEvent event) {
    String authorizationHeader = event.getHeaders().get("Authorization");
    if (authorizationHeader == null) {
      return makeErrorResponse(403, "No authorization header in request");
    }
    if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
      return makeErrorResponse(403, "Authorization must be a bearer token");
    }
    String authorizationBearer = authorizationHeader.substring(BEARER_PREFIX.length());
    if (authorizationDAO.getAuthorization(authorizationBearer).isEmpty()) {
      return makeErrorResponse(403, "Authorization bearer is not authorized");
    }
    return handleAuthorizedRequest(event);
  }

  protected @NonNull APIGatewayProxyResponseEvent makeJSONResponse(final int status, final @NonNull Object body) {
    try {
      return new APIGatewayProxyResponseEvent()
          .withIsBase64Encoded(false)
          .withStatusCode(status)
          .withHeaders(Map.of(
              "Content-Type",
              "application/json"))
          .withBody(objectMapper.writeValueAsString(body));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  protected @NonNull APIGatewayProxyResponseEvent makeErrorResponse(final int status, final @NonNull String body) {
    return new APIGatewayProxyResponseEvent()
        .withIsBase64Encoded(false)
        .withStatusCode(status)
        .withHeaders(Map.of(
            "Content-Type",
            "text/plain"))
        .withBody(body);
  }

  protected @NonNull APIGatewayProxyResponseEvent makeErrorResponse(final int status, final @NonNull Exception e) {
    return makeErrorResponse(status, e.getClass().getSimpleName() + ": " + e.getMessage());
  }

  protected abstract @NonNull APIGatewayProxyResponseEvent handleAuthorizedRequest(
      final @NonNull APIGatewayProxyRequestEvent event);

}
