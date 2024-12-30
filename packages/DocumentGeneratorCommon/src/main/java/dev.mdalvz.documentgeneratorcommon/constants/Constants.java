package dev.mdalvz.documentgeneratorcommon.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  public static final String DOCUMENTS_TABLE_NAME_KEY = "DOCUMENTS_TABLE_NAME_KEY";

  public static final String AUTHORIZATIONS_TABLE_NAME_KEY = "AUTHORIZATIONS_TABLE_NAME_KEY";

  public static final String RENDER_QUEUE_URL_KEY = "RENDER_QUEUE_URL_KEY";

  public static final String INPUT_BUCKET_NAME_KEY = "INPUT_BUCKET_NAME_KEY";

  public static final String OUTPUT_BUCKET_NAME_KEY = "OUTPUT_BUCKET_NAME_KEY";

  public static final String DOCUMENTS_TABLE_INPUT_KEY_INDEX_NAME = "inputKey";

}
