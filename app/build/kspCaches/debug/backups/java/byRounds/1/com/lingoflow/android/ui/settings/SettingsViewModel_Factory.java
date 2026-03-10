package com.lingoflow.android.ui.settings;

import com.lingoflow.android.data.repository.SettingsRepository;
import com.lingoflow.android.llm.GeminiApiClient;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<GeminiApiClient> geminiApiClientProvider;

  public SettingsViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<GeminiApiClient> geminiApiClientProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.geminiApiClientProvider = geminiApiClientProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsRepositoryProvider.get(), geminiApiClientProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<GeminiApiClient> geminiApiClientProvider) {
    return new SettingsViewModel_Factory(settingsRepositoryProvider, geminiApiClientProvider);
  }

  public static SettingsViewModel newInstance(SettingsRepository settingsRepository,
      GeminiApiClient geminiApiClient) {
    return new SettingsViewModel(settingsRepository, geminiApiClient);
  }
}
