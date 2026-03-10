package com.lingoflow.android.llm;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PromptLoader_Factory implements Factory<PromptLoader> {
  private final Provider<Context> contextProvider;

  public PromptLoader_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PromptLoader get() {
    return newInstance(contextProvider.get());
  }

  public static PromptLoader_Factory create(Provider<Context> contextProvider) {
    return new PromptLoader_Factory(contextProvider);
  }

  public static PromptLoader newInstance(Context context) {
    return new PromptLoader(context);
  }
}
