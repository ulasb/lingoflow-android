package com.lingoflow.android.ui.history;

import androidx.lifecycle.SavedStateHandle;
import com.lingoflow.android.data.repository.HistoryRepository;
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
public final class HistoryDetailViewModel_Factory implements Factory<HistoryDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  public HistoryDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
  }

  @Override
  public HistoryDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), historyRepositoryProvider.get());
  }

  public static HistoryDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    return new HistoryDetailViewModel_Factory(savedStateHandleProvider, historyRepositoryProvider);
  }

  public static HistoryDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      HistoryRepository historyRepository) {
    return new HistoryDetailViewModel(savedStateHandle, historyRepository);
  }
}
