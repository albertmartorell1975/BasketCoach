package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Insights
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.navigation.AnalysisRoute
import com.martorell.albert.basketcoach.ui.navigation.BasketCoachAppState
import com.martorell.albert.basketcoach.ui.navigation.ChatRoute
import com.martorell.albert.basketcoach.ui.navigation.DashboardRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardStaticStateTest {

    @Test
    fun navOptions_configuredCorrectlyWithTopLevelRoutes() {
        val options = BasketCoachAppState.NAV_OPTIONS
        assertEquals(3, options.size)

        assertEquals(DashboardRoute, options[0].route)
        assertEquals(R.string.nav_dashboard, options[0].titleRes)
        assertEquals(Icons.Filled.Dashboard, options[0].icon)

        assertEquals(AnalysisRoute, options[1].route)
        assertEquals(R.string.nav_analysis, options[1].titleRes)
        assertEquals(Icons.Filled.Insights, options[1].icon)

        assertEquals(ChatRoute(), options[2].route)
        assertEquals(R.string.nav_chat, options[2].titleRes)
        assertEquals(Icons.Filled.Forum, options[2].icon)
    }

    @Test
    fun phase1Summary_containsStrictResourceIdsAndStats() {
        val summary = Phase1PreviewData.summary
        assertEquals(3, summary.wins)
        assertEquals(11, summary.losses)
        assertEquals(14, summary.totalGames)
        assertEquals(21.4, summary.winPercentage, 0.01)
        assertEquals(R.string.topbar_category, summary.categoryRes)
        assertEquals(R.string.hero_club_name, summary.teamNameRes)
    }

    @Test
    fun evolutionRounds_containStrictNonNullableBadges() {
        val rounds = Phase1PreviewData.evolution.rounds
        assertEquals(14, rounds.size)

        val winRounds = rounds.filter { it.isWin }
        assertEquals(3, winRounds.size)
        assertTrue(winRounds.all { it.scoreBadge.isNotEmpty() })

        val lossRounds = rounds.filter { !it.isWin }
        assertEquals(11, lossRounds.size)
        assertTrue(lossRounds.all { it.scoreBadge.isEmpty() })

        assertEquals("W 71-68", rounds[2].scoreBadge)
        assertEquals("W 68-65", rounds[8].scoreBadge)
        assertEquals("W 67-65", rounds[9].scoreBadge)
    }

    @Test
    fun kpisAndMatches_matchPhase1Totals() {
        val kpis = Phase1PreviewData.kpis
        assertEquals(64.6, kpis.scoring.averageScore, 0.01)
        assertEquals(22.8, kpis.rebounds.averageRebounds, 0.01)
        assertEquals(40.5, kpis.shooting.efgPercentage, 0.01)
        assertEquals(0.59, kpis.gameControl.astToRatio, 0.01)

        val matches = Phase1PreviewData.matches
        assertEquals(14, matches.size)
        assertEquals(3, matches.count { it.isWin })
        assertEquals(11, matches.count { !it.isWin })
    }
}
