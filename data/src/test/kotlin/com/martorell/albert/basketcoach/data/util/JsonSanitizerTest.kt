package com.martorell.albert.basketcoach.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class JsonSanitizerTest {

    @Test
    fun stripMarkdownCodeFences_removesJsonCodeFences() {
        val rawWithFences = """
            ```json
            {"key": "value"}
            ```
        """.trimIndent()

        val sanitized = JsonSanitizer.stripMarkdownCodeFences(rawWithFences)
        assertEquals("{\"key\": \"value\"}", sanitized)
    }

    @Test
    fun stripMarkdownCodeFences_whenNoFencesPresent_returnsTrimmedContent() {
        val rawPlain = "  {\"key\": \"value\"}  "
        val sanitized = JsonSanitizer.stripMarkdownCodeFences(rawPlain)
        assertEquals("{\"key\": \"value\"}", sanitized)
    }
}
