package dev.mdalvz.documentgeneratorcommon.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SQSModule extends AbstractModule {

  @Provides
  @Singleton
  public SqsClient provideSqsClient() {
    return SqsClient.create();
  }

}
