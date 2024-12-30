package dev.mdalvz.documentgeneratorlambda.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import dev.mdalvz.documentgeneratorcommon.module.DynamoDBModule;
import dev.mdalvz.documentgeneratorcommon.module.S3Module;
import dev.mdalvz.documentgeneratorcommon.module.SQSModule;
import dev.mdalvz.documentgeneratorlambda.operation.CreateDocumentOperation;
import dev.mdalvz.documentgeneratorlambda.operation.Operation;

public class DocumentGeneratorLambdaModule extends AbstractModule {

  @Override
  public void configure() {
    install(new DynamoDBModule());
    install(new SQSModule());
    install(new S3Module());

    Multibinder<Operation> operationBinder = Multibinder.newSetBinder(binder(), Operation.class);
    operationBinder.addBinding().to(CreateDocumentOperation.class);
  }

  @Provides
  @Singleton
  public ObjectMapper provideObjectMapper() {
    return new ObjectMapper();
  }

}
