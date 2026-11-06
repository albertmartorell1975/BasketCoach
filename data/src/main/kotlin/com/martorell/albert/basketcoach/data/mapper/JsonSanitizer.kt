package com.martorell.albert.basketcoach.data.mapper

/**
 * Utility responsible for sanitizing raw LLM responses before JSON deserialization.
 *
 * LLM endpoints commonly return structured JSON wrapped inside Markdown code fences
 * (e.g. ```` ```json ... ``` ````). This sanitizer strips leading and trailing markdown wrappers
 * while preserving the raw JSON content intact.
 */
object JsonSanitizer {

    private const val CODE_FENCE = "```"

    /**
     * Strips Markdown code fences wrapping JSON response content.
     *
     * @param raw The raw string received from the AI completion endpoint.
     * @return Sanitized string with enclosing Markdown fences removed and trimmed.
     */
    fun stripMarkdownCodeFences(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.startsWith(CODE_FENCE)) {
            val lines = trimmed.lines()
            val withoutFirst = if (lines.isNotEmpty() && lines.first().startsWith(CODE_FENCE)) {
                lines.drop(1)
            } else {
                lines
            }
            val withoutLast = if (withoutFirst.isNotEmpty() && withoutFirst.last().startsWith(CODE_FENCE)) {
                withoutFirst.dropLast(1)
            } else {
                withoutFirst
            }
            return withoutLast.joinToString("\n").trim()
        }
        return trimmed
    }
}
