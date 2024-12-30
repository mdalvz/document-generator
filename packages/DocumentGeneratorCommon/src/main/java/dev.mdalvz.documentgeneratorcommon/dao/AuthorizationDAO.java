package dev.mdalvz.documentgeneratorcommon.dao;

import dev.mdalvz.documentgeneratorcommon.model.AuthorizationRecord;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

public class AuthorizationDAO {

  private final @NonNull DynamoDbTable<AuthorizationRecord> authorizationsTable;

  public AuthorizationDAO(final @NonNull DynamoDbEnhancedClient dynamoDbEnhancedClient,
                          final @NonNull String tableName) {
    this.authorizationsTable = dynamoDbEnhancedClient.table(
        tableName,
        TableSchema.fromBean(AuthorizationRecord.class));
  }

  public Optional<AuthorizationRecord> getAuthorization(final @NonNull String authorizationId) {
    return Optional.ofNullable(this.authorizationsTable.getItem(Key.builder()
        .partitionValue(authorizationId)
        .build()));
  }

}
