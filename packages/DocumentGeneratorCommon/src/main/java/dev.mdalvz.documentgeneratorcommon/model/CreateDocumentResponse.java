package dev.mdalvz.documentgeneratorcommon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentResponse {

  @JsonProperty
  private @NonNull String documentId;

  @JsonProperty
  private @NonNull String putUrl;

}
