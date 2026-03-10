package com.lingoflow.android.data.repository;

import com.google.gson.Gson;
import com.lingoflow.android.llm.LlmClientProvider;
import com.lingoflow.android.llm.PromptLoader;
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
public final class ChatRepository_Factory implements Factory<ChatRepository> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<ScenarioRepository> scenarioRepositoryProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<LlmClientProvider> llmClientProvider;

  private final Provider<PromptLoader> promptLoaderProvider;

  private final Provider<Gson> gsonProvider;

  public ChatRepository_Factory(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<ScenarioRepository> scenarioRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<LlmClientProvider> llmClientProvider, Provider<PromptLoader> promptLoaderProvider,
      Provider<Gson> gsonProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.scenarioRepositoryProvider = scenarioRepositoryProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.llmClientProvider = llmClientProvider;
    this.promptLoaderProvider = promptLoaderProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public ChatRepository get() {
    return newInstance(settingsRepositoryProvider.get(), scenarioRepositoryProvider.get(), historyRepositoryProvider.get(), llmClientProvider.get(), promptLoaderProvider.get(), gsonProvider.get());
  }

  public static ChatRepository_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<ScenarioRepository> scenarioRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<LlmClientProvider> llmClientProvider, Provider<PromptLoader> promptLoaderProvider,
      Provider<Gson> gsonProvider) {
    return new ChatRepository_Factory(settingsRepositoryProvider, scenarioRepositoryProvider, historyRepositoryProvider, llmClientProvider, promptLoaderProvider, gsonProvider);
  }

  public static ChatRepository newInstance(SettingsRepository settingsRepository,
      ScenarioRepository scenarioRepository, HistoryRepository historyRepository,
      LlmClientProvider llmClientProvider, PromptLoader promptLoader, Gson gson) {
    return new ChatRepository(settingsRepository, scenarioRepository, historyRepository, llmClientProvider, promptLoader, gson);
  }
}
