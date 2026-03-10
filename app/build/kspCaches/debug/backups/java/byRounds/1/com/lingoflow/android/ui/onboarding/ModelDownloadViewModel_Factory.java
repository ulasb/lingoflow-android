package com.lingoflow.android.ui.onboarding;

import com.lingoflow.android.llm.MediaPipeLlmClient;
import com.lingoflow.android.llm.ModelDownloadManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ModelDownloadViewModel_Factory implements Factory<ModelDownloadViewModel> {
  private final Provider<ModelDownloadManager> modelDownloadManagerProvider;

  private final Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider;

  public ModelDownloadViewModel_Factory(Provider<ModelDownloadManager> modelDownloadManagerProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    this.modelDownloadManagerProvider = modelDownloadManagerProvider;
    this.mediaPipeLlmClientProvider = mediaPipeLlmClientProvider;
  }

  @Override
  public ModelDownloadViewModel get() {
    return newInstance(modelDownloadManagerProvider.get(), mediaPipeLlmClientProvider.get());
  }

  public static ModelDownloadViewModel_Factory create(
      Provider<ModelDownloadManager> modelDownloadManagerProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    return new ModelDownloadViewModel_Factory(modelDownloadManagerProvider, mediaPipeLlmClientProvider);
  }

  public static ModelDownloadViewModel newInstance(ModelDownloadManager modelDownloadManager,
      MediaPipeLlmClient mediaPipeLlmClient) {
    return new ModelDownloadViewModel(modelDownloadManager, mediaPipeLlmClient);
  }
}
