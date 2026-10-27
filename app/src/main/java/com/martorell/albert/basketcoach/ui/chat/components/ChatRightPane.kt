package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

/**
 * Right workbench pane containing the assistant header, scrollable conversation stream (or empty state),
 * and the fixed bottom message input bar.
 *
 * @param messages List of conversation messages ([ChatMessageUiModel]).
 * @param inputText Current text in the input bar.
 * @param isSending Whether a message send action is currently in flight.
 * @param onInputChange Callback fired when the input text changes.
 * @param onSend Callback fired to dispatch the current input text.
 * @param onDrillOptionSelect Callback fired when a formative drill choice is clicked.
 * @param modifier Composable modifier for layout customization.
 */
@Composable
fun ChatRightPane(
    messages: List<ChatMessageUiModel>,
    inputText: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onDrillOptionSelect: (ChatDrillOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PiaNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sports,
                            contentDescription = null,
                            tint = PiaWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = stringResource(R.string.chat_assistant_title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaNavy
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PiaSuccessGreen)
                            )
                            Text(
                                text = stringResource(R.string.chat_assistant_status),
                                fontSize = 12.sp,
                                color = PiaTextMuted
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PiaSurfaceContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = PiaNavy,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.chat_baseline_pill),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PiaNavy
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    ChatWelcomeState()
                } else {
                    ChatMessageStream(
                        messages = messages,
                        onDrillOptionSelect = onDrillOptionSelect
                    )
                }
            }

            ChatInputBar(
                text = inputText,
                onTextChange = onInputChange,
                onSend = onSend,
                isSending = isSending
            )
        }
    }
}

@BcThemePreview
@Composable
private fun ChatRightPanePreview() {
    ChatRightPane(
        messages = ChatPreviewData.initialMessages,
        inputText = "",
        isSending = false,
        onInputChange = {},
        onSend = {},
        onDrillOptionSelect = {}
    )
}
