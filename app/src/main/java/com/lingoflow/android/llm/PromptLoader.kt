package com.lingoflow.android.llm

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = mutableMapOf<String, String>()

    fun load(filename: String, variables: Map<String, String> = emptyMap()): String {
        val template = cache.getOrPut(filename) {
            context.assets.open("prompts/$filename").bufferedReader().readText()
        }
        var result = template
        for ((key, value) in variables) {
            result = result.replace("{{$key}}", value)
        }
        return result
    }
}
