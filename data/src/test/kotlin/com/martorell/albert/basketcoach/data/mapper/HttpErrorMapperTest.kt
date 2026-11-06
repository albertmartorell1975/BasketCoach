package com.martorell.albert.basketcoach.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test

class HttpErrorMapperTest {

    @Test
    fun `parse extracts clean message from Google Cloud array error JSON`() {
        val rawGoogleJson = """
            [
              {
                "error": {
                  "code": 400,
                  "message": "Invalid Auth key.",
                  "status": "INVALID_ARGUMENT"
                }
              }
            ]
        """.trimIndent()

        val parsed = HttpErrorMapper.parse(rawGoogleJson)
        assertEquals("Invalid Auth key.", parsed)
    }

    @Test
    fun `parse extracts clean message from Google Cloud single object error JSON`() {
        val rawGoogleJson = """
            {
              "error": {
                "code": 404,
                "message": "This model models/gemini-2.5-flash is no longer available to new users.",
                "status": "NOT_FOUND"
              }
            }
        """.trimIndent()

        val parsed = HttpErrorMapper.parse(rawGoogleJson)
        assertEquals("This model models/gemini-2.5-flash is no longer available to new users.", parsed)
    }

    @Test
    fun `parse extracts clean message from OpenAI and FastAPI format error JSON`() {
        val rawOpenAiJson = """{"error": {"message": "Incorrect API key provided"}}"""
        assertEquals("Incorrect API key provided", HttpErrorMapper.parse(rawOpenAiJson))

        val rawFastApiDetail = """{"detail": "Authentication token expired"}"""
        assertEquals("Authentication token expired", HttpErrorMapper.parse(rawFastApiDetail))

        val rawMessage = """{"message": "Rate limit exceeded"}"""
        assertEquals("Rate limit exceeded", HttpErrorMapper.parse(rawMessage))
    }

    @Test
    fun `parse returns fallback or raw string when not valid JSON`() {
        assertEquals("Gateway Timeout", HttpErrorMapper.parse("Gateway Timeout", "fallback"))
        assertEquals("fallback", HttpErrorMapper.parse("", "fallback"))
        assertEquals("fallback", HttpErrorMapper.parse(null, "fallback"))
    }
}
