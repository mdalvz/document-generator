package dev.mdalvz.documentgeneratorlambda.operation;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import dev.mdalvz.documentgeneratorcommon.dao.AuthorizationDAO;
import dev.mdalvz.documentgeneratorcommon.dao.DocumentDAO;
import dev.mdalvz.documentgeneratorcommon.model.CreateDocumentRequest;
import dev.mdalvz.documentgeneratorcommon.model.CreateDocumentResponse;
import dev.mdalvz.documentgeneratorcommon.model.DocumentRecord;
import dev.mdalvz.documentgeneratorcommon.model.DocumentStatus;
import lombok.NonNull;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class CreateDocumentOperation extends PostOperation<CreateDocumentRequest, CreateDocumentResponse> {

  private final @NonNull DocumentDAO documentDAO;

  private final @NonNull S3Presigner s3Presigner;

  @Inject
  public CreateDocumentOperation(final @NonNull ObjectMapper objectMapper,
                                 final @NonNull AuthorizationDAO authorizationDAO,
                                 final @NonNull DocumentDAO documentDAO,
                                 final @NonNull S3Presigner s3Presigner) {
    super(objectMapper, authorizationDAO, "/documents", CreateDocumentRequest.class);
    this.documentDAO = documentDAO;
    this.s3Presigner = s3Presigner;
  }

  @Override
  protected @NonNull CreateDocumentResponse handlePost(
      final @NonNull APIGatewayProxyRequestEvent event,
      final @NonNull CreateDocumentRequest body) {
    final String documentId = UUID.randomUUID().toString();
    final String inputKey = UUID.randomUUID() + "." + body.getInputType().getExtension();
    documentDAO.putDocument(DocumentRecord.builder()
        .documentId(documentId)
        .status(DocumentStatus.CREATED)
        .name(body.getName())
        .inputKey(inputKey)
        .inputType(body.getInputType())
        .build());
    final String putUrl = s3Presigner.presignPutObject(PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofDays(1))
        .putObjectRequest(PutObjectRequest.builder()
            .bucket(System.getenv(Constants.INPUT_BUCKET_NAME_KEY))
            .key(inputKey)
            .build())
        .build())
        .url()
        .toString();
    return CreateDocumentResponse.builder()
        .documentId(documentId)
        .putUrl(putUrl)
        .build();
  }

}
