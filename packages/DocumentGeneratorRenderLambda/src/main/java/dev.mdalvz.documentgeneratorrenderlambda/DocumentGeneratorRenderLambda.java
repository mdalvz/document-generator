package dev.mdalvz.documentgeneratorrenderlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.Inject;
import dev.mdalvz.documentgeneratorcommon.dao.DocumentDAO;
import dev.mdalvz.documentgeneratorrenderlambda.module.DocumentGeneratorRenderLambdaModule;

public class DocumentGeneratorRenderLambda implements RequestHandler<SQSEvent, Boolean> {

  @Inject
  DocumentDAO documentDAO;

  public DocumentGeneratorRenderLambda() {
    Guice.createInjector(new DocumentGeneratorRenderLambdaModule())
        .injectMembers(this);
  }

  @Override
  public Boolean handleRequest(SQSEvent sqsEvent, Context context) {
    return true;
  }

}
