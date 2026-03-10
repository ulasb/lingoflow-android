package com.lingoflow.android.ui.chat;

import androidx.lifecycle.SavedStateHandle;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ChatRepository> chatRepositoryProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<ScenarioRepository> scenarioRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider;

  public ChatViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ChatRepository> chatRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<ScenarioRepository> scenarioRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.chatRepositoryProvider = chatRepositoryProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.scenarioRepositoryProvider = scenarioRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.mediaPipeLlmClientProvider = mediaPipeLlmClientProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(savedStateHandleProvider.get(), chatRepositoryProvider.get(), historyRepositoryProvider.get(), scenarioRepositoryProvider.get(), settingsRepositoryProvider.get(), mediaPipeLlmClientProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ChatRepository> chatRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<ScenarioRepository> scenarioRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<MediaPipeLlmClient> mediaPipeLlmClientProvider) {
    return new ChatViewModel_Factory(savedStateHandleProvider, chatRepositoryProvider, historyRepositoryProvider, scenarioRepositoryProvider, settingsRepositoryProvider, mediaPipeLlmClientProvider);
  }

  public static ChatViewModel newInstance(SavedStateHandle savedStateHandle,
      ChatRepository chatRepository, HistoryRepository historyRepository,
      ScenarioRepository scenarioRepository, SettingsRepository settingsRepository,
      MediaPipeLlmClient mediaPipeLlmClient) {
    return new ChatViewModel(savedStateHandle, chatRepository, historyRepository, scenarioRepository, settingsRepository, mediaPipeLlmClient);
  }
}
