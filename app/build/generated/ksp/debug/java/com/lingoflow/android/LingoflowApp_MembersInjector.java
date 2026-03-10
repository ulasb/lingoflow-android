package com.lingoflow.android;

import com.lingoflow.android.llm.MediaPipeLlmClient;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class LingoflowApp_MembersInjector implements MembersInjector<LingoflowApp> {
  private final Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider;

  public LingoflowApp_MembersInjector(Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    this.mediaPipeLlmClientProvider = mediaPipeLlmClientProvider;
  }

  public static MembersInjector<LingoflowApp> create(
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    return new LingoflowApp_MembersInjector(mediaPipeLlmClientProvider);
  }

  @Override
  public void injectMembers(LingoflowApp instance) {
    injectMediaPipeLlmClient(instance, mediaPipeLlmClientProvider.get());
  }

  @InjectedFieldSignature("com.lingoflow.android.LingoflowApp.mediaPipeLlmClient")
  public static void injectMediaPipeLlmClient(LingoflowApp instance,
      MediaPipeLlmClient mediaPipeLlmClient) {
    instance.mediaPipeLlmClient = mediaPipeLlmClient;
  }
}
