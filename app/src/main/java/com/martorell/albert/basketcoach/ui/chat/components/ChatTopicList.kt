package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Left pane component presenting the list of Phase 1 tactical intervention areas and the
 * Socratic Pia methodology card.
 *
 * @param topics List of topic cards to render ([ChatTopicCardUiModel]).
 * @param activeTopicId Identifier of the currently selected topic.
 * @param onTopicClick Callback triggered when a card is clicked.
 * @param modifier Composable modifier for layout customization.
 */
@Composable
fun ChatTopicList(
    topics: List<ChatTopicCardUiModel>,
    activeTopicId: String,
    onTopicClick: (ChatTopicCardUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.chat_topics_header_tag),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PiaTacticalOrange
            )
            Text(
                text = stringResource(R.string.chat_topics_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PiaNavy
            )
            Text(
                text = stringResource(R.string.chat_topics_subtitle),
                fontSize = 12.sp,
                color = PiaTextMuted,
                lineHeight = 16.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            topics.forEach { topic ->
                ChatTopicCard(
                    model = topic,
                    isActive = topic.id == activeTopicId,
                    onClick = { onTopicClick(topic) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PiaSurfaceContainerLow)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = PiaNavy,
                    modifier = Modifier.size(18.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.chat_methodology_title),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaNavy
                    )
                    Text(
                        text = stringResource(R.string.chat_methodology_desc),
                        fontSize = 12.sp,
                        color = PiaTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@BcThemePreview
@Composable
private fun ChatTopicListPreview() {
    ChatTopicList(
        topics = ChatPreviewData.defaultTopicList,
        activeTopicId = "rebound_control",
        onTopicClick = {}
    )
}
