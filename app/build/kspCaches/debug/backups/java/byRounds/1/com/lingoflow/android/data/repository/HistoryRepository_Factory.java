package com.lingoflow.android.data.repository;

import com.lingoflow.android.data.dao.HistoryDao;
import com.lingoflow.android.data.dao.MessageDao;
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
public final class HistoryRepository_Factory implements Factory<HistoryRepository> {
  private final Provider<HistoryDao> historyDaoProvider;

  private final Provider<MessageDao> messageDaoProvider;

  public HistoryRepository_Factory(Provider<HistoryDao> historyDaoProvider,
      Provider<MessageDao> messageDaoProvider) {
    this.historyDaoProvider = historyDaoProvider;
    this.messageDaoProvider = messageDaoProvider;
  }

  @Override
  public HistoryRepository get() {
    return newInstance(historyDaoProvider.get(), messageDaoProvider.get());
  }

  public static HistoryRepository_Factory create(Provider<HistoryDao> historyDaoProvider,
      Provider<MessageDao> messageDaoProvider) {
    return new HistoryRepository_Factory(historyDaoProvider, messageDaoProvider);
  }

  public static HistoryRepository newInstance(HistoryDao historyDao, MessageDao messageDao) {
    return new HistoryRepository(historyDao, messageDao);
  }
}
