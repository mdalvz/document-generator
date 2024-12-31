package dev.mdalvz.documentgeneratorrenderlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.Inject;
import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import dev.mdalvz.documentgeneratorcommon.dao.DocumentDAO;
import dev.mdalvz.documentgeneratorcommon.model.DocumentStatus;
import dev.mdalvz.documentgeneratorrenderlambda.lib.FileServer;
import dev.mdalvz.documentgeneratorrenderlambda.lib.Renderer;
import dev.mdalvz.documentgeneratorrenderlambda.module.DocumentGeneratorRenderLambdaModule;
import lombok.NonNull;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class DocumentGeneratorRenderLambda implements RequestHandler<SQSEvent, Boolean> {

  @Inject
  DocumentDAO documentDAO;

  @Inject
  S3Client s3Client;

  @Inject
  FileServer fileServer;

  @Inject
  Renderer renderer;

  public DocumentGeneratorRenderLambda() {
    Guice.createInjector(new DocumentGeneratorRenderLambdaModule())
        .injectMembers(this);
  }

  @Override
  public @NonNull Boolean handleRequest(final @NonNull SQSEvent event,
                                        final @NonNull Context context) {
    event.getRecords()
        .stream()
        .map(SQSEvent.SQSMessage::getBody)
        .map(documentId -> this.documentDAO.getDocument(documentId))
        .flatMap(Optional::stream)
        .filter(documentRecord -> documentRecord.getStatus().equals(DocumentStatus.QUEUED))
        .forEach(documentRecord -> {
          documentDAO.putDocument(documentRecord.toBuilder()
              .status(DocumentStatus.STARTED)
              .build());
          try {
            fileServer.reset();
            try (final OutputStream index = new FileOutputStream(fileServer.getRoot().getPath() + "/index.html")) {
              index.write("<div>Hello World! 123 Test Test</div>".getBytes(StandardCharsets.UTF_8));
            }
            final ByteBuffer output = renderer.render();
            final String outputKey = UUID.randomUUID() + ".pdf";
            s3Client.putObject(PutObjectRequest.builder()
                .bucket(System.getenv(Constants.OUTPUT_BUCKET_NAME_KEY))
                .key(outputKey)
                .contentType("application/pdf")
                .build(),
                RequestBody.fromByteBuffer(output));
            documentDAO.putDocument(documentRecord.toBuilder()
                .status(DocumentStatus.SUCCEEDED)
                .outputKey(outputKey)
                .build());
//              s3Client.getObject(GetObjectRequest.builder()
//                  .bucket(System.getenv(Constants.INPUT_BUCKET_NAME_KEY))
//                  .key(documentRecord.getInputKey())
//                  .build());
          } catch (final Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            documentDAO.putDocument(documentRecord.toBuilder()
                .status(DocumentStatus.FAILED)
                .outputKey(null)
                .build());
          }
        });
    return true;
  }

}
