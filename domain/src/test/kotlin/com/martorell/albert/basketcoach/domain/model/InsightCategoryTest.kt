package com.martorell.albert.basketcoach.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class InsightCategoryTest {

    @Test
    fun `fromString parses case-insensitively and trims whitespace across domain enums`() {
        assertEquals(InsightCategory.OFFENSE, InsightCategory.fromString("OFFENSE"))
        assertEquals(InsightCategory.OFFENSE, InsightCategory.fromString("  offense  "))
        assertEquals(InsightCategory.DEFENSE, InsightCategory.fromString("\tDEFENSE\n"))
        assertEquals(InsightCategory.SYNERGY, InsightCategory.fromString("synergy"))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString("general"))

        assertEquals(InsightType.STRENGTH, InsightType.fromString("strength"))
        assertEquals(InsightType.IMPROVEMENT, InsightType.fromString("IMPROVEMENT"))

        assertEquals(MetricSentiment.POSITIVE, MetricSentiment.fromString("positive"))
        assertEquals(MetricSentiment.NEGATIVE, MetricSentiment.fromString("NEGATIVE"))
        assertEquals(MetricSentiment.NEUTRAL, MetricSentiment.fromString("neutral"))
    }

    @Test
    fun `fromString handles unknown or blank inputs according to strictness contract`() {
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString("UNKNOWN_CATEGORY"))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString("   "))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString(null))

        try {
            InsightType.fromString("UNKNOWN")
            fail("Expected IllegalArgumentException for unknown InsightType")
        } catch (_: IllegalArgumentException) {
            // Expected fail-fast contract
        }

        try {
            MetricSentiment.fromString("UNKNOWN")
            fail("Expected IllegalArgumentException for unknown MetricSentiment")
        } catch (_: IllegalArgumentException) {
            // Expected fail-fast contract
        }
    }
}
