package dev.mdalvz.documentgeneratorcommon.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationRecord {

  @Getter(onMethod_ = @__(@DynamoDbPartitionKey))
  @NonNull
  String authorizationId;

}
