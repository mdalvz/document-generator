package dev.mdalvz.documentgeneratorcommon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {

  @JsonProperty
  private @NonNull String name;

  @JsonProperty
  private @NonNull DocumentInputType inputType;

}
