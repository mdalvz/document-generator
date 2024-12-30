package dev.mdalvz.documentgeneratorrenderlambda.module;

import com.google.inject.AbstractModule;
import dev.mdalvz.documentgeneratorcommon.module.DynamoDBModule;
import dev.mdalvz.documentgeneratorcommon.module.S3Module;

public class DocumentGeneratorRenderLambdaModule extends AbstractModule {

  @Override
  public void configure() {
    install(new DynamoDBModule());
    install(new S3Module());
  }

}
