package com.lingoflow.android.data.repository;

import com.lingoflow.android.data.dao.ScenarioDao;
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
public final class ScenarioRepository_Factory implements Factory<ScenarioRepository> {
  private final Provider<ScenarioDao> scenarioDaoProvider;

  public ScenarioRepository_Factory(Provider<ScenarioDao> scenarioDaoProvider) {
    this.scenarioDaoProvider = scenarioDaoProvider;
  }

  @Override
  public ScenarioRepository get() {
    return newInstance(scenarioDaoProvider.get());
  }

  public static ScenarioRepository_Factory create(Provider<ScenarioDao> scenarioDaoProvider) {
    return new ScenarioRepository_Factory(scenarioDaoProvider);
  }

  public static ScenarioRepository newInstance(ScenarioDao scenarioDao) {
    return new ScenarioRepository(scenarioDao);
  }
}
