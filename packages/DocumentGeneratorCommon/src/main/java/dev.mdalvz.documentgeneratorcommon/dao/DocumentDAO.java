package dev.mdalvz.documentgeneratorcommon.dao;

import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import dev.mdalvz.documentgeneratorcommon.model.DocumentRecord;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Optional;

public class DocumentDAO {

  private final @NonNull DynamoDbTable<DocumentRecord> documentsTable;

  private final @NonNull DynamoDbIndex<DocumentRecord> documentsTableInputKeyIndex;

  public DocumentDAO(final @NonNull DynamoDbEnhancedClient dynamoDbEnhancedClient,
                     final @NonNull String tableName) {
    this.documentsTable = dynamoDbEnhancedClient.table(
        tableName,
        TableSchema.fromBean(DocumentRecord.class));
    this.documentsTableInputKeyIndex = this.documentsTable.index(
        Constants.DOCUMENTS_TABLE_INPUT_KEY_INDEX_NAME);
  }

  public @NonNull Optional<DocumentRecord> getDocument(final @NonNull String documentId) {
    return Optional.ofNullable(this.documentsTable.getItem(Key.builder()
        .partitionValue(documentId)
        .build()));
  }

  public @NonNull Optional<DocumentRecord> getDocumentByInputKey(final @NonNull String inputKey) {
    return this.documentsTableInputKeyIndex.query(QueryEnhancedRequest.builder()
        .queryConditional(QueryConditional.keyEqualTo(Key.builder()
            .partitionValue(inputKey)
            .build()))
        .build())
        .stream()
        .findFirst()
        .flatMap((e) -> e.items().stream().findFirst());
  }

  public void putDocument(final @NonNull DocumentRecord record) {
    this.documentsTable.putItem(record);
  }

}
