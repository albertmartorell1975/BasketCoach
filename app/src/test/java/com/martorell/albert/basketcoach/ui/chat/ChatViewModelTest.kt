package com.martorell.albert.basketcoach.ui.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.usecases.ai.GenerateInsightsUseCase
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

    private fun sampleGame(round: Int, isWin: Boolean): Game {
        val homePoints = if (isWin) 70 else 50
        val awayPoints = 60
        val stats = TeamStats(
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
            points = homePoints
        )
        val oppStats = TeamStats(
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
            points = awayPoints
        )
        return Game(
            round = round,
            date = "2023-10-08",
            time = "17:45",
            venue = Venue("Sabadell", "Pavello Nord", "Carrer Ronda 1"),
            homeTeam = OUR_TEAM_NAME,
            awayTeam = "Rival",
            finalScore = Score(homePoints, awayPoints),
            homeStats = stats,
            awayStats = oppStats
        )
    }

    private class FakeGameRepository(val games: List<Game>) : GameRepository {
        var gamesResult: Result<List<Game>, DomainError> = Result.Success(games)
        override suspend fun getPhase1Games(): Result<List<Game>, DomainError> = gamesResult
        override suspend fun getGameByRound(round: Int): Result<Game, DomainError> = throw UnsupportedOperationException()
    }

    private class FakeAiAnalysisRepository : AiAnalysisRepository {
        var insightsResult: Result<List<Insight>, DomainError> = Result.Success(
            listOf(
                Insight(
                    id = "topic_rebound",
                    title = "Control del Rebot Defensiu",
                    description = "Description",
                    category = InsightCategory.DEFENSE,
                    relatedGameRounds = listOf(1),
                    type = InsightType.IMPROVEMENT,
                    contextTag = "Defensa",
                    metric = InsightMetric("Reb", "13.2", "", "", "", "", MetricSentiment.NEGATIVE),
                    priority = "Crítica #1",
                    footerNote = "",
                    exploreChatPrompt = "Explora el rebot defensiu"
                ),
                Insight(
                    id = "topic_ast_to",
                    title = "Ràtio Assistències / Pèrdues",
                    description = "Description",
                    category = InsightCategory.OFFENSE,
                    relatedGameRounds = listOf(1),
                    type = InsightType.IMPROVEMENT,
                    contextTag = "Atac",
                    metric = InsightMetric("AST", "10.5", "", "", "", "", MetricSentiment.POSITIVE),
                    priority = "Mitjana",
                    footerNote = "",
                    exploreChatPrompt = "Explora la ràtio"
                )
            )
        )
        override suspend fun getInsights(prompt: com.martorell.albert.basketcoach.domain.prompt.AiAnalysisPrompt): Result<List<Insight>, DomainError> = insightsResult
        override suspend fun getDrills(prompt: com.martorell.albert.basketcoach.domain.prompt.AiAnalysisPrompt): Result<List<Drill>, DomainError> = throw UnsupportedOperationException()
    }

    private class FakeChatRepository : ChatRepository {
        val messagesByTopic = mutableMapOf<String, MutableList<ChatMessage>>()
        var sendResult: Result<ChatMessage, DomainError>? = null

        override suspend fun getMessages(topicId: String): Result<List<ChatMessage>, DomainError> =
            Result.Success(messagesByTopic[topicId]?.toList() ?: emptyList())

        override suspend fun sendMessage(
            messageText: String,
            games: List<Game>?,
            topicId: String
        ): Result<ChatMessage, DomainError> {
            val result = sendResult
            if (result != null) return result

            val coachMsg = ChatMessage("c-$topicId-${messagesByTopic[topicId]?.size ?: 0}", MessageSender.COACH, messageText, 1000L)
            val aiMsg = ChatMessage("a-$topicId-${messagesByTopic[topicId]?.size ?: 0}", MessageSender.AI, "AI response to: $messageText", 2000L)
            val list = messagesByTopic.getOrPut(topicId) { mutableListOf() }
            list.add(coachMsg)
            list.add(aiMsg)
            return Result.Success(aiMsg)
        }
    }

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var fakeAiAnalysisRepository: FakeAiAnalysisRepository
    private lateinit var fakeChatRepository: FakeChatRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeGameRepository = FakeGameRepository(listOf(sampleGame(1, true)))
        fakeAiAnalysisRepository = FakeAiAnalysisRepository()
        fakeChatRepository = FakeChatRepository()

        val getPhase1GamesUseCase = GetPhase1GamesUseCase(fakeGameRepository)
        val generateInsightsUseCase = GenerateInsightsUseCase(fakeGameRepository, fakeAiAnalysisRepository)
        val getMessagesUseCase = GetChatMessagesUseCase(fakeChatRepository)
        val sendChatMessageUseCase = SendChatMessageUseCase(fakeGameRepository, fakeChatRepository)
        val mapper = ChatUiMapper()

        viewModel = ChatViewModel(
            getPhase1GamesUseCase = getPhase1GamesUseCase,
            generateInsightsUseCase = generateInsightsUseCase,
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
    fun loadChat_withInitialPrompt_selectsMatchingImprovementTopicAndDispatchesMessageOnce() = runTest {
        assertEquals(ChatUiContent.Loading, viewModel.uiState.value.content)

        viewModel.loadChat("Explora el rebot defensiu")
        advanceUntilIdle()

        val content = viewModel.uiState.value.content
        assertTrue(content is ChatUiContent.Success)
        val success = content as ChatUiContent.Success
        assertEquals("topic_rebound", success.activeTopicId)
        assertEquals(2, success.messages.size)
        assertEquals("Explora el rebot defensiu", success.messages[0].text)
        assertEquals(MessageSender.COACH, success.messages[0].sender)
        assertEquals(MessageSender.AI, success.messages[1].sender)
    }

    @Test
    fun onTopicSelected_switchesActiveTopicAndLoadsThreadMessages() = runTest {
        viewModel.loadChat()
        advanceUntilIdle()

        val initialSuccess = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals("topic_rebound", initialSuccess.activeTopicId)

        viewModel.sendMessage("Help with defensive rebound")
        advanceUntilIdle()

        val topic2 = initialSuccess.topicList.first { it.id == "topic_ast_to" }
        viewModel.onTopicSelected(topic2)
        advanceUntilIdle()

        var success = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals("topic_ast_to", success.activeTopicId)
        assertTrue(success.messages.isEmpty())

        val topic1 = success.topicList.first { it.id == "topic_rebound" }
        viewModel.onTopicSelected(topic1)
        advanceUntilIdle()

        success = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals("topic_rebound", success.activeTopicId)
        assertEquals(2, success.messages.size)
        assertEquals("Help with defensive rebound", success.messages[0].text)
    }

    @Test
    fun loadChat_whenGamesFail_transitionsToErrorAndRecoversOnRetry() = runTest {
        fakeGameRepository.gamesResult = Result.Error(DomainError.Network.NoInternet)

        viewModel.loadChat()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is ChatUiContent.Error)
        assertEquals(DomainError.Network.NoInternet, (viewModel.uiState.value.content as ChatUiContent.Error).error)

        fakeGameRepository.gamesResult = Result.Success(listOf(sampleGame(1, true)))
        viewModel.onRetry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is ChatUiContent.Success)
        val success = viewModel.uiState.value.content as ChatUiContent.Success
        assertEquals("topic_rebound", success.activeTopicId)
    }
}
