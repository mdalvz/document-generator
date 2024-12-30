package dev.mdalvz.documentgeneratorcommon.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public class S3Module extends AbstractModule {

  @Provides
  @Singleton
  public S3Client provideS3Client() {
    return S3Client.create();
  }

  @Provides
  @Singleton
  public S3Presigner provideS3Presigner() {
    return S3Presigner.create();
  }

}
