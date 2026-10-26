package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab

@Composable
fun InsightsDrillsPane(
    insights: List<Insight>,
    drills: List<Drill>,
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        DashboardTab.ALL to "All (${insights.size + drills.size})",
        DashboardTab.INSIGHTS to "Insights (${insights.size})",
        DashboardTab.DRILLS to "Drills (${drills.size})"
    )

    Column(
        modifier = modifier
            .testTag("InsightsDrillsPane")
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        PrimaryTabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("DashboardTabRow")
        ) {
            tabs.forEach { (tab, title) ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("DashboardTab_${tab.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("InsightsDrillsLazyColumn"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (selectedTab == DashboardTab.ALL || selectedTab == DashboardTab.INSIGHTS) {
                if (insights.isNotEmpty()) {
                    item(key = "header_insights") {
                        Text(
                            text = "Tactical AI Insights",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                    items(insights, key = { "ins_${it.id}" }) { insight ->
                        InsightCard(insight = insight)
                    }
                }
            }

            if (selectedTab == DashboardTab.ALL || selectedTab == DashboardTab.DRILLS) {
                if (drills.isNotEmpty()) {
                    item(key = "header_drills") {
                        Text(
                            text = "Recommended Practice Drills",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(drills, key = { "drill_${it.id}" }) { drill ->
                        DrillCard(drill = drill)
                    }
                }
            }
        }
    }
}
