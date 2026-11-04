package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Vertically scrollable list of tactical improvement topics and the unconstrained inquiry card.
 *
 * Renders the section header ("ÀREES DE MILLORA") and a scrollable column of [ChatTopicCard] items,
 * allowing Coach Jordi to seamlessly navigate between focused tactical conversation threads.
 *
 * @param topics The list of [ChatTopicCardUiModel] entries representing AI improvement areas and open inquiry.
 * @param activeTopicId Unique identifier of the currently highlighted or active topic.
 * @param onTopicClick Callback invoked when a topic card is selected.
 * @param modifier Optional [Modifier] for container customization.
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
                text = stringResource(R.string.chat_topics_subtitle, 14),
                fontSize = 12.sp,
                color = PiaTextMuted,
                lineHeight = 16.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topics.forEach { topic ->
                ChatTopicCard(
                    model = topic,
                    isActive = topic.id == activeTopicId,
                    onClick = { onTopicClick(topic) }
                )
            }
        }
    }
}

@BcThemePreview
@Composable
private fun ChatTopicListPreview() {
    ChatTopicList(
        topics = ChatPreviewData.defaultTopicList,
        activeTopicId = ChatPreviewData.defaultTopicList.first().id,
        onTopicClick = {}
    )
}
