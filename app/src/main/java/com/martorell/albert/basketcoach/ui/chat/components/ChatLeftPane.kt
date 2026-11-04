package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Left workbench pane for the Tablet Landscape Chat screen.
 *
 * Permanently displays the list of tactical improvement areas alongside the unconstrained
 * "Consulta Lliure" card ([ChatTopicList]), keeping intervention areas visible at all times.
 *
 * @param topics The list of [ChatTopicCardUiModel] items to render.
 * @param activeTopicId Unique identifier of the currently active topic card.
 * @param onTopicClick Callback triggered when the coach selects a topic card.
 * @param modifier Optional [Modifier] for container customization.
 */
@Composable
fun ChatLeftPane(
    topics: List<ChatTopicCardUiModel>,
    activeTopicId: String,
    onTopicClick: (ChatTopicCardUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    ChatTopicList(
        topics = topics,
        activeTopicId = activeTopicId,
        onTopicClick = onTopicClick,
        modifier = modifier.fillMaxWidth()
    )
}

@BcThemePreview
@Composable
private fun ChatLeftPanePreview() {
    ChatLeftPane(
        topics = ChatPreviewData.defaultTopicList,
        activeTopicId = ChatPreviewData.defaultTopicList.first().id,
        onTopicClick = {}
    )
}
