package com.lingoflow.android.llm;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LlmClientProvider_Factory implements Factory<LlmClientProvider> {
  private final Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider;

  private final Provider<GeminiApiClient> geminiApiClientProvider;

  public LlmClientProvider_Factory(Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider,
      Provider<GeminiApiClient> geminiApiClientProvider) {
    this.mediaPipeLlmClientProvider = mediaPipeLlmClientProvider;
    this.geminiApiClientProvider = geminiApiClientProvider;
  }

  @Override
  public LlmClientProvider get() {
    return newInstance(mediaPipeLlmClientProvider.get(), geminiApiClientProvider.get());
  }

  public static LlmClientProvider_Factory create(
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider,
      Provider<GeminiApiClient> geminiApiClientProvider) {
    return new LlmClientProvider_Factory(mediaPipeLlmClientProvider, geminiApiClientProvider);
  }

  public static LlmClientProvider newInstance(MediaPipeLlmClient mediaPipeLlmClient,
      GeminiApiClient geminiApiClient) {
    return new LlmClientProvider(mediaPipeLlmClient, geminiApiClient);
  }
}
