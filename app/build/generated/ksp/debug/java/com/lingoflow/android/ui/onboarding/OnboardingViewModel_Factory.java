package com.lingoflow.android.ui.onboarding;

import com.lingoflow.android.data.repository.SettingsRepository;
import com.lingoflow.android.llm.GeminiApiClient;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider;

  private final Provider<GeminiApiClient> geminiApiClientProvider;

  private final Provider<ModelDownloadManager> modelDownloadManagerProvider;

  public OnboardingViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider,
      Provider<GeminiApiClient> geminiApiClientProvider,
      Provider<ModelDownloadManager> modelDownloadManagerProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.mediaPipeLlmClientProvider = mediaPipeLlmClientProvider;
    this.geminiApiClientProvider = geminiApiClientProvider;
    this.modelDownloadManagerProvider = modelDownloadManagerProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(settingsRepositoryProvider.get(), mediaPipeLlmClientProvider.get(), geminiApiClientProvider.get(), modelDownloadManagerProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider,
      Provider<GeminiApiClient> geminiApiClientProvider,
      Provider<ModelDownloadManager> modelDownloadManagerProvider) {
    return new OnboardingViewModel_Factory(settingsRepositoryProvider, mediaPipeLlmClientProvider, geminiApiClientProvider, modelDownloadManagerProvider);
  }

  public static OnboardingViewModel newInstance(SettingsRepository settingsRepository,
      MediaPipeLlmClient mediaPipeLlmClient, GeminiApiClient geminiApiClient,
      ModelDownloadManager modelDownloadManager) {
    return new OnboardingViewModel(settingsRepository, mediaPipeLlmClient, geminiApiClient, modelDownloadManager);
  }
}
