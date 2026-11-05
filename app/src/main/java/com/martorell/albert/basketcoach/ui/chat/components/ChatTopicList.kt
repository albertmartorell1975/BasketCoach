package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
 * Renders the section header ("ÀREES DE MILLORA") and a scrollable [LazyColumn] of [ChatTopicCard] items,
 * automatically centering the [activeTopicId] card upon selection or navigation hand-off.
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
    val listState = rememberLazyListState()

    LaunchedEffect(activeTopicId, topics) {
        val index = topics.indexOfFirst { it.id == activeTopicId }
        if (index >= 0) {
            val layoutInfo = listState.layoutInfo
            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            if (viewportHeight > 0) {
                val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }
                val itemSize = visibleItem?.size ?: (viewportHeight / 3)
                val offset = -((viewportHeight - itemSize) / 2).coerceAtLeast(0)
                listState.animateScrollToItem(index, offset)
            } else {
                listState.animateScrollToItem(index)
            }
        }
    }

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

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = topics,
                key = { it.id }
            ) { topic ->
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
