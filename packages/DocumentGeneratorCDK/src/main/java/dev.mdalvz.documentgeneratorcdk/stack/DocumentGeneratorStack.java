package dev.mdalvz.documentgeneratorcdk.stack;

import dev.mdalvz.documentgeneratorcommon.constants.Constants;
import lombok.Getter;
import lombok.NonNull;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSourceProps;
import software.amazon.awscdk.services.s3.*;
import software.amazon.awscdk.services.s3.notifications.LambdaDestination;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;

import java.util.List;
import java.util.Map;

@Getter
public class DocumentGeneratorStack extends Stack {

  private final @NonNull Table documentsTable;

  private final @NonNull Table authorizationsTable;

  private final @NonNull Queue renderQueue;

  private final @NonNull Bucket inputBucket;

  private final @NonNull Bucket outputBucket;

  private final @NonNull Function function;

  private final @NonNull Function fileNotificationFunction;

  private final @NonNull Function renderFunction;

  public DocumentGeneratorStack(final @NonNull App app) {
    super(app, "DocumentGeneratorStack");

    this.documentsTable = new Table(this, "DocumentsTable", TableProps.builder()
        .removalPolicy(RemovalPolicy.DESTROY)
        .partitionKey(Attribute.builder()
            .type(AttributeType.STRING)
            .name("documentId")
            .build())
        .build());

    this.documentsTable.addGlobalSecondaryIndex(GlobalSecondaryIndexProps.builder()
        .indexName(Constants.DOCUMENTS_TABLE_INPUT_KEY_INDEX_NAME)
        .partitionKey(Attribute.builder()
            .type(AttributeType.STRING)
            .name("inputKey")
            .build())
        .projectionType(ProjectionType.ALL)
        .build());

    this.authorizationsTable = new Table(this,"AuthorizationsTable", TableProps.builder()
        .removalPolicy(RemovalPolicy.DESTROY)
        .partitionKey(Attribute.builder()
            .type(AttributeType.STRING)
            .name("authorizationId")
            .build())
        .build());

    this.renderQueue = new Queue(this, "RenderQueue", QueueProps.builder()
        .removalPolicy(RemovalPolicy.DESTROY)
        .visibilityTimeout(Duration.seconds(60))
        .build());

    this.inputBucket = new Bucket(this, "InputBucket", BucketProps.builder()
        .removalPolicy(RemovalPolicy.DESTROY)
        .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
        .build());

    this.outputBucket = new Bucket(this, "OutputBucket", BucketProps.builder()
        .removalPolicy(RemovalPolicy.DESTROY)
        .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
        .build());

    final Map<String, String> functionEnvironment = Map.of(
        Constants.DOCUMENTS_TABLE_NAME_KEY,
        this.documentsTable.getTableName(),
        Constants.AUTHORIZATIONS_TABLE_NAME_KEY,
        this.authorizationsTable.getTableName(),
        Constants.RENDER_QUEUE_URL_KEY,
        this.renderQueue.getQueueUrl(),
        Constants.INPUT_BUCKET_NAME_KEY,
        this.inputBucket.getBucketName(),
        Constants.OUTPUT_BUCKET_NAME_KEY,
        this.outputBucket.getBucketName());

    this.function = new Function(this, "Function", FunctionProps.builder()
        .runtime(Runtime.JAVA_21)
        .timeout(Duration.seconds(30))
        .memorySize(512)
        .code(Code.fromAsset("../DocumentGeneratorLambda/build/libs/DocumentGeneratorLambda-1.0.jar"))
        .handler("dev.mdalvz.documentgeneratorlambda.DocumentGeneratorLambda::handleRequest")
        .environment(functionEnvironment)
        .build());

    this.fileNotificationFunction = new Function(this, "FileNotificationFunction", FunctionProps.builder()
        .runtime(Runtime.JAVA_21)
        .timeout(Duration.seconds(30))
        .memorySize(512)
        .code(Code.fromAsset("../DocumentGeneratorFileNotificationLambda/build/libs/DocumentGeneratorFileNotificationLambda-1.0.jar"))
        .handler("dev.mdalvz.documentgeneratorfilenotificationlambda.DocumentGeneratorFileNotificationLambda::handleRequest")
        .environment(functionEnvironment)
        .build());

    this.renderFunction = new Function(this, "RenderFunction", FunctionProps.builder()
        .runtime(Runtime.FROM_IMAGE)
        .timeout(Duration.seconds(60))
        .memorySize(2048)
        .code(Code.fromAssetImage("../DocumentGeneratorRenderLambda"))
        .handler(Handler.FROM_IMAGE)
        .environment(functionEnvironment)
        .build());

    List.of(this.function, this.fileNotificationFunction, this.renderFunction).forEach(e -> {
      this.documentsTable.grantReadWriteData(e);
      this.authorizationsTable.grantReadWriteData(e);
      this.renderQueue.grantSendMessages(e);
      this.inputBucket.grantReadWrite(e);
      this.outputBucket.grantReadWrite(e);
    });

    this.inputBucket.addEventNotification(
        EventType.OBJECT_CREATED,
        new LambdaDestination(this.fileNotificationFunction));

    this.renderFunction.addEventSource(new SqsEventSource(this.renderQueue, SqsEventSourceProps.builder()
        .batchSize(10)
        .build()));
  }

}
