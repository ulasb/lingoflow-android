package com.lingoflow.android.ui.dashboard;

import com.lingoflow.android.data.repository.ChatRepository;
import com.lingoflow.android.data.repository.HistoryRepository;
import com.lingoflow.android.data.repository.ScenarioRepository;
import com.lingoflow.android.data.repository.SettingsRepository;
import com.lingoflow.android.llm.MediaPipeLlmClient;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<ScenarioRepository> scenarioRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<ChatRepository> chatRepositoryProvider;

  private final Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider;

  public DashboardViewModel_Factory(Provider<ScenarioRepository> scenarioRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<ChatRepository> chatRepositoryProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    this.scenarioRepositoryProvider = scenarioRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.chatRepositoryProvider = chatRepositoryProvider;
    this.mediaPipeLlmClientProvider = mediaPipeLlmClientProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(scenarioRepositoryProvider.get(), settingsRepositoryProvider.get(), historyRepositoryProvider.get(), chatRepositoryProvider.get(), mediaPipeLlmClientProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<ScenarioRepository> scenarioRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<ChatRepository> chatRepositoryProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    return new DashboardViewModel_Factory(scenarioRepositoryProvider, settingsRepositoryProvider, historyRepositoryProvider, chatRepositoryProvider, mediaPipeLlmClientProvider);
  }

  public static DashboardViewModel newInstance(ScenarioRepository scenarioRepository,
      SettingsRepository settingsRepository, HistoryRepository historyRepository,
      ChatRepository chatRepository, MediaPipeLlmClient mediaPipeLlmClient) {
    return new DashboardViewModel(scenarioRepository, settingsRepository, historyRepository, chatRepository, mediaPipeLlmClient);
  }
}
