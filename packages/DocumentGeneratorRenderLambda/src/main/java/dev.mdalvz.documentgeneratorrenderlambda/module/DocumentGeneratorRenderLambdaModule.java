package dev.mdalvz.documentgeneratorrenderlambda.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.mdalvz.documentgeneratorcommon.module.DynamoDBModule;
import dev.mdalvz.documentgeneratorcommon.module.S3Module;
import dev.mdalvz.documentgeneratorrenderlambda.lib.FileServer;

public class DocumentGeneratorRenderLambdaModule extends AbstractModule {

  @Override
  public void configure() {
    install(new DynamoDBModule());
    install(new S3Module());
  }

  @Provides
  @Singleton
  public FileServer provideFileServer() {
    return FileServer.getInstance();
  }

}
