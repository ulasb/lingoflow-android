package com.lingoflow.android.di;

import com.lingoflow.android.data.dao.ScenarioDao;
import com.lingoflow.android.data.database.LingoflowDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideScenarioDaoFactory implements Factory<ScenarioDao> {
  private final Provider<LingoflowDatabase> dbProvider;

  public DatabaseModule_ProvideScenarioDaoFactory(Provider<LingoflowDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ScenarioDao get() {
    return provideScenarioDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideScenarioDaoFactory create(
      Provider<LingoflowDatabase> dbProvider) {
    return new DatabaseModule_ProvideScenarioDaoFactory(dbProvider);
  }

  public static ScenarioDao provideScenarioDao(LingoflowDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideScenarioDao(db));
  }
}
