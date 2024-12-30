package dev.mdalvz.documentgeneratorfilenotificationlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.inject.Guice;
import com.google.inject.Inject;
import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import dev.mdalvz.documentgeneratorcommon.dao.DocumentDAO;
import dev.mdalvz.documentgeneratorcommon.model.DocumentStatus;
import dev.mdalvz.documentgeneratorfilenotificationlambda.module.DocumentGeneratorFileNotificationLambdaModule;
import lombok.NonNull;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class DocumentGeneratorFileNotificationLambda implements RequestHandler<S3EventNotification, Boolean> {

  @Inject
  DocumentDAO documentDAO;

  @Inject
  SqsClient sqsClient;

  public DocumentGeneratorFileNotificationLambda() {
    Guice.createInjector(new DocumentGeneratorFileNotificationLambdaModule())
        .injectMembers(this);
  }

  @Override
  public @NonNull Boolean handleRequest(final @NonNull S3EventNotification event,
                                        final @NonNull Context context) {
    final String inputBucketName = System.getenv(Constants.INPUT_BUCKET_NAME_KEY);
    final String renderQueueUrl = System.getenv(Constants.RENDER_QUEUE_URL_KEY);
    event.getRecords()
        .stream()
        .filter(record -> record
            .getS3()
            .getBucket()
            .getName()
            .equals(inputBucketName))
        .map(record -> record
            .getS3()
            .getObject()
            .getKey())
        .toList()
        .parallelStream()
        .forEach(inputKey -> {
          documentDAO.getDocumentByInputKey(inputKey).ifPresent(documentRecord -> {
            if (documentRecord.getStatus().equals(DocumentStatus.CREATED)) {
              documentDAO.putDocument(documentRecord.toBuilder()
                  .status(DocumentStatus.QUEUED)
                  .build());
              sqsClient.sendMessage(SendMessageRequest.builder()
                  .queueUrl(renderQueueUrl)
                  .messageBody(documentRecord.getDocumentId())
                  .build());
            }
          });
        });
    return true;
  }

}
