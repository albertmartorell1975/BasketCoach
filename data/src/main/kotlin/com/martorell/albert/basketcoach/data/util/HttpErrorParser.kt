package com.martorell.albert.basketcoach.data.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Utility to extract clean, human-readable error descriptions from raw JSON HTTP error response bodies.
 *
 * Dissects various industry error JSON structures:
 * - Google Cloud / Gemini REST format: `{"error": {"code": 400, "message": "...", "status": "..."}}`
 * - Google Cloud batch/array format: `[{"error": {"code": 400, "message": "...", ...}}]`
 * - OpenAI error format: `{"error": {"message": "..."}}`
 * - FastAPI / DevExpert formats: `{"message": "..."}`, `{"detail": "..."}`, `{"error": "..."}`
 */
object HttpErrorParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Extracts a clean message from [rawBody]. If [rawBody] is blank or cannot be parsed,
     * returns [fallbackMessage] or [rawBody] stripped of outer whitespace.
     *
     * @param rawBody The raw HTTP response body string.
     * @param fallbackMessage Fallback string if body is empty or null.
     * @return Extracted human-readable message without JSON markup.
     */
    fun parse(rawBody: String?, fallbackMessage: String = ""): String {
        if (rawBody.isNullOrBlank()) {
            return fallbackMessage.trim()
        }

        val trimmed = rawBody.trim()
        return try {
            val element = json.parseToJsonElement(trimmed)
            extractMessage(element) ?: trimmed
        } catch (_: Exception) {
            trimmed
        }
    }

    private fun extractMessage(element: JsonElement): String? {
        return when (element) {
            is JsonArray -> {
                element.firstOrNull()?.let { extractMessage(it) }
            }
            is JsonObject -> {
                val errorField = element["error"]
                when (errorField) {
                    is JsonObject -> errorField["message"]?.jsonPrimitive?.content
                    is JsonArray -> errorField.firstOrNull()?.let { extractMessage(it) }
                    else -> errorField?.jsonPrimitive?.content
                } ?: element["message"]?.jsonPrimitive?.content
                  ?: element["detail"]?.let { detail ->
                      when (detail) {
                          is JsonArray -> detail.firstOrNull()?.let { extractMessage(it) }
                          is JsonObject -> detail["msg"]?.jsonPrimitive?.content ?: extractMessage(detail)
                          else -> detail.jsonPrimitive.content
                      }
                  }
                  ?: element["description"]?.jsonPrimitive?.content
            }
            else -> element.jsonPrimitive.content
        }
    }
}