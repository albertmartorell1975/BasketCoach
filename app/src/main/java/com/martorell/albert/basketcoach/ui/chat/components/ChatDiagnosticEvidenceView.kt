package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.ui.chat.components.evidence.EvidenceCourtCard
import com.martorell.albert.basketcoach.ui.chat.components.evidence.EvidenceMethodologyBanner
import com.martorell.albert.basketcoach.ui.chat.components.evidence.EvidenceMetricsCard
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

@Composable
fun ChatDiagnosticEvidenceView(
    model: ChatDiagnosticEvidenceUiModel,
    onBackToTopics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EvidenceMetricsCard(
            model = model,
            onBackToTopics = onBackToTopics
        )

        EvidenceCourtCard(
            model = model
        )

        EvidenceMethodologyBanner(
            title = model.methodologyTitle,
            cycle = model.methodologyCycle
        )
    }
}

@BcThemePreview
@Composable
private fun ChatDiagnosticEvidenceViewPreview() {
    ChatDiagnosticEvidenceView(
        model = ChatPreviewData.defaultDiagnosticEvidence,
        onBackToTopics = {}
    )
}
