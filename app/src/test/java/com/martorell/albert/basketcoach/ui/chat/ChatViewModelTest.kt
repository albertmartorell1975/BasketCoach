package com.martorell.albert.basketcoach.ui.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicId
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.usecases.chat.GetChatMessagesUseCase
import com.martorell.albert.basketcoach.usecases.chat.SendChatMessageUseCase
import com.martorell.albert.basketcoach.usecases.game.GetPhase1GamesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeGameRepository : GameRepository {
        var gamesResult: Result<List<Game>, DomainError> = Result.Success(
            listOf(
                Game(
                    round = 1,
                    date = "2023-10-08",
                    time = "17:45",
                    venue = Venue("Sabadell", "Pavello Nord", "Carrer Ronda 1"),
                    homeTeam = OUR_TEAM_NAME,
                    awayTeam = "Rival",
                    finalScore = Score(70, 60),
                    homeStats = TeamStats(
                        ftPct = 66.7,
                        fg2Pct = 50.0,
                        fg3Pct = 33.3,
                        efgPct = 45.0,
                        offensiveRebounds = 10,
                        defensiveRebounds = 20,
                        totalRebounds = 30,
                        turnovers = 14,
                        assists = 12,
                        astToRatio = 0.86,
                        steals = 5,
                        blocks = 2,
                        secondChancePoints = 8,
                        fastBreakPoints = 6,
                        personalFouls = 18,
                        points = 70
                    ),
                    awayStats = TeamStats(
                        ftPct = 66.7,
                        fg2Pct = 42.8,
                        fg3Pct = 33.3,
                        efgPct = 45.0,
                        offensiveRebounds = 8,
                        defensiveRebounds = 22,
                        totalRebounds = 30,
                        turnovers = 16,
                        assists = 10,
                        astToRatio = 0.62,
                        steals = 6,
                        blocks = 1,
                        secondChancePoints = 6,
                        fastBreakPoints = 4,
                        personalFouls = 15,
                        points = 60
                    )
                )
            )
        )
        override suspend fun getPhase1Games(): Result<List<Game>, DomainError> = gamesResult
        override suspend fun getGameByRound(round: Int): Result<Game, DomainError> = throw UnsupportedOperationException()
    }

    private class FakeChatRepository : ChatRepository {
        val messages = mutableListOf<ChatMessage>()
        var sendResult: Result<ChatMessage, DomainError>? = null

        override suspend fun getMessages(): Result<List<ChatMessage>, DomainError> = Result.Success(messages.toList())

        override suspend fun sendMessage(
            messageText: String,
            games: List<Game>?
        ): Result<ChatMessage, DomainError> {
            val result = sendResult
            if (result != null) return result

            val coachMsg = ChatMessage("c-1", MessageSender.COACH, messageText, 1000L)
            val aiMsg = ChatMessage("a-1", MessageSender.AI, "AI Response", 2000L)
            messages.add(coachMsg)
            messages.add(aiMsg)
            return Result.Success(aiMsg)
        }
    }

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var fakeChatRepository: FakeChatRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeGameRepository = FakeGameRepository()
        fakeChatRepository = FakeChatRepository()
        val getPhase1GamesUseCase = GetPhase1GamesUseCase(fakeGameRepository)
        val getMessagesUseCase = GetChatMessagesUseCase(fakeChatRepository)
        val sendChatMessageUseCase = SendChatMessageUseCase(fakeGameRepository, fakeChatRepository)
        val mapper = ChatUiMapper()

        viewModel = ChatViewModel(
            getPhase1GamesUseCase = getPhase1GamesUseCase,
            getChatMessagesUseCase = getMessagesUseCase,
            sendChatMessageUseCase = sendChatMessageUseCase,
            chatUiMapper = mapper
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadChat_withInitialPrompt_dispatchesMessageAndSetsEvidenceMode() = runTest {
        assertEquals(ChatUiContent.Loading, viewModel.uiState.value.content)

        viewModel.loadChat("Explora el rebot defensiu")
        advanceUntilIdle()

        val content = viewModel.uiState.value.content
        assertTrue(content is ChatUiContent.Success)
        val success = content as ChatUiContent.Success
        assertEquals(ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE, success.leftPaneMode)
        assertEquals(2, success.messages.size)
        assertEquals("Explora el rebot defensiu", success.messages[0].text)
    }

    @Test
    fun loadChat_whenGamesFail_transitionsToErrorAndRecoversOnRetry() = runTest {
        fakeGameRepository.gamesResult = Result.Error(DomainError.Network.NoInternet)

        viewModel.loadChat()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is ChatUiContent.Error)
        assertEquals(DomainError.Network.NoInternet, (viewModel.uiState.value.content as ChatUiContent.Error).error)

        fakeGameRepository.gamesResult = Result.Success(emptyList())
        viewModel.onRetry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is ChatUiContent.Error)
        assertEquals(DomainError.Local.NotFound, (viewModel.uiState.value.content as ChatUiContent.Error).error)
    }

    @Test
    fun onTopicSelected_updatesActiveTopicIdAndSwitchesPaneWhenEvidenceAvailable() = runTest {
        viewModel.loadChat()
        advanceUntilIdle()

        val initialSuccess = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals(ChatTopicId.REBOUND_CONTROL, initialSuccess.activeTopicId)
        assertEquals(ChatLeftPaneMode.TOPIC_LIST, initialSuccess.leftPaneMode)

        val nonEvidenceTopic = initialSuccess.topicList.first { !it.hasDiagnosticEvidence }
        viewModel.onTopicSelected(nonEvidenceTopic)
        var updated = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals(nonEvidenceTopic.id, updated.activeTopicId)
        assertEquals(ChatLeftPaneMode.TOPIC_LIST, updated.leftPaneMode)

        val evidenceTopic = initialSuccess.topicList.first { it.hasDiagnosticEvidence }
        viewModel.onTopicSelected(evidenceTopic)
        updated = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals(evidenceTopic.id, updated.activeTopicId)
        assertEquals(ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE, updated.leftPaneMode)

        // Switching back to non-evidence topic returns left pane to TOPIC_LIST
        viewModel.onTopicSelected(nonEvidenceTopic)
        updated = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals(nonEvidenceTopic.id, updated.activeTopicId)
        assertEquals(ChatLeftPaneMode.TOPIC_LIST, updated.leftPaneMode)
    }

    @Test
    fun onInputTextChanged_updatesInputDraft() = runTest {
        viewModel.loadChat()
        advanceUntilIdle()

        viewModel.onInputTextChanged("Nova pregunta tàctica")
        val success = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals("Nova pregunta tàctica", success.inputText)
    }
}
