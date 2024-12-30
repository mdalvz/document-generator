package dev.mdalvz.documentgeneratorcommon.model;

import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRecord {

  @Getter(onMethod_ = @__(@DynamoDbPartitionKey))
  @NonNull
  String documentId;

  @Getter
  @NonNull
  DocumentStatus status;

  @Getter
  @NonNull
  String name;

  @Getter(onMethod_ = @__(@DynamoDbSecondaryPartitionKey(
      indexNames = {Constants.DOCUMENTS_TABLE_INPUT_KEY_INDEX_NAME})))
  @NonNull
  String inputKey;

  @Getter
  @NonNull
  DocumentInputType inputType;

  @Getter
  String outputKey;

}
