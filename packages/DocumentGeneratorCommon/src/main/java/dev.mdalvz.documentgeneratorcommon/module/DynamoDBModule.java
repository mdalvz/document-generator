package dev.mdalvz.documentgeneratorcommon.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import dev.mdalvz.documentgeneratorcommon.dao.AuthorizationDAO;
import dev.mdalvz.documentgeneratorcommon.dao.DocumentDAO;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBModule extends AbstractModule {

  @Provides
  @Singleton
  public DynamoDbClient provideDynamoDbClient() {
    return DynamoDbClient.create();
  }

  @Provides
  @Singleton
  public DynamoDbEnhancedClient provideDynamoDbEnhancedClient(final @NonNull DynamoDbClient dynamoDbClient) {
    return DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dynamoDbClient)
        .build();
  }

  @Provides
  @Singleton
  public DocumentDAO provideDocumentDAO(final @NonNull DynamoDbEnhancedClient dynamoDbEnhancedClient) {
    return new DocumentDAO(
        dynamoDbEnhancedClient,
        System.getenv(Constants.DOCUMENTS_TABLE_NAME_KEY));
  }

  @Provides
  @Singleton
  public AuthorizationDAO provideAuthorizationDAO(final @NonNull DynamoDbEnhancedClient dynamoDbEnhancedClient) {
    return new AuthorizationDAO(
        dynamoDbEnhancedClient,
        System.getenv(Constants.AUTHORIZATIONS_TABLE_NAME_KEY));
  }

}
