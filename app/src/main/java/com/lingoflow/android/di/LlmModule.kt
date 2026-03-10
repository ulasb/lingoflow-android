package com.lingoflow.android.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// LlmClientProvider, MediaPipeLlmClient, and GeminiApiClient use @Inject constructor
// and are automatically discovered by Hilt. This module exists as a placeholder for
// any future manual LLM-related bindings.
@Module
@InstallIn(SingletonComponent::class)
object LlmModule
