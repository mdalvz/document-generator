package dev.mdalvz.documentgeneratorfilenotificationlambda.module;

import com.google.inject.AbstractModule;
import dev.mdalvz.documentgeneratorcommon.module.DynamoDBModule;
import dev.mdalvz.documentgeneratorcommon.module.SQSModule;

public class DocumentGeneratorFileNotificationLambdaModule extends AbstractModule {

  @Override
  public void configure() {
    install(new DynamoDBModule());
  }

}
