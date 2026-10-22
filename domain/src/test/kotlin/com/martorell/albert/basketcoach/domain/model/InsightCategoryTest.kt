package com.martorell.albert.basketcoach.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class InsightCategoryTest {

    @Test
    fun `fromString correctly parses uppercase and lowercase names`() {
        assertEquals(InsightCategory.OFFENSE, InsightCategory.fromString("OFFENSE"))
        assertEquals(InsightCategory.OFFENSE, InsightCategory.fromString("offense"))
        assertEquals(InsightCategory.DEFENSE, InsightCategory.fromString("defense"))
        assertEquals(InsightCategory.DEFENSE, InsightCategory.fromString("DEFENSE"))
        assertEquals(InsightCategory.SYNERGY, InsightCategory.fromString("synergy"))
        assertEquals(InsightCategory.SYNERGY, InsightCategory.fromString("SYNERGY"))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString("general"))
    }

    @Test
    fun `fromString handles whitespace trimming`() {
        assertEquals(InsightCategory.OFFENSE, InsightCategory.fromString("  offense  "))
        assertEquals(InsightCategory.DEFENSE, InsightCategory.fromString("\tDEFENSE\n"))
    }

    @Test
    fun `fromString defaults to GENERAL on unknown string or null`() {
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString("UNKNOWN_CATEGORY"))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString(""))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString("   "))
        assertEquals(InsightCategory.GENERAL, InsightCategory.fromString(null))
    }
}
