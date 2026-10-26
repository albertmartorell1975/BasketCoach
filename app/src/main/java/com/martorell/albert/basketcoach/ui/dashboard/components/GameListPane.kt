package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.ComponentPreview
import com.martorell.albert.basketcoach.ui.theme.LossRed
import com.martorell.albert.basketcoach.ui.theme.WinGreen

@Composable
fun GameListPane(
    games: List<Game>,
    selectedGame: Game?,
    onGameSelected: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalWins = games.count { it.isWin }
    val totalLosses = games.count { !it.isWin }
    val avgPointsScored = if (games.isNotEmpty()) games.map { it.teamScore }.average() else 0.0
    val avgPointsConceded = if (games.isNotEmpty()) games.map { it.opponentScore }.average() else 0.0

    Column(
        modifier = modifier
            .testTag("GameListPane")
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TeamRecordCard"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.phase_1_record),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$totalWins ${stringResource(R.string.wins_badge)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WinGreen
                        )
                        Text(
                            text = stringResource(R.string.record_divider),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$totalLosses ${stringResource(R.string.losses_badge)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LossRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.avg_scored_format, avgPointsScored),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.avg_conceded_format, avgPointsConceded),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (selectedGame != null) {
            Spacer(modifier = Modifier.height(10.dp))
            SelectedGameDetailCard(game = selectedGame)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.match_fixtures_format, games.size),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("GameListLazyColumn"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(games, key = { it.round }) { game ->
                GameCard(
                    game = game,
                    isSelected = selectedGame?.round == game.round,
                    onClick = { onGameSelected(game) }
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun GameListPanePreview() {
    BasketCoachTheme {
        GameListPane(
            games = DashboardPreviewData.sampleGames,
            selectedGame = DashboardPreviewData.sampleGames[2],
            onGameSelected = {}
        )
    }
}
