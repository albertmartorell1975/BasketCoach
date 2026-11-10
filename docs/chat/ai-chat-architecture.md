# AI Chat & Socratic Assistant Architecture

> Core architectural specification for the BasketCoach tactical chat assistant.  
> See also: [ARCHITECTURE.md](../../ARCHITECTURE.md) and [ai-tactical-analysis-structured-outputs.md](ai-tactical-analysis-structured-outputs.md).

---

## 1. Executive Summary

BasketCoach provides a **Tablet Landscape Socratic AI Tactical Assistant** designed specifically for the head coach of **Bàsquet Pia Sabadell**.

This system is architected around **tactical rigor, cost predictability, and strict context security**:
* **Empirical Grounding**: Answers are grounded directly in Phase 1 performance data (14 games).
* **Multi-Thread Focus**: Conversations are isolated between an unconstrained general thread (**"Consulta Lliure"**) and dedicated **Improvement Area Threads**.
* **Two-Tier AI Defense**: Low-latency deterministic pre-screening via a dedicated policy evaluation model (**JEV**) protects the heavy generative model from prompt injection and off-topic chatter.
* **Multilingual Parity**: Seamless dynamic support for Catalan and Spanish based on the active device locale.

---

## 2. The JEV Semantic Firewall

Generative LLMs are expensive and slow when used simply to classify incoming queries. To solve this, BasketCoach implements a pre-screening firewall using the **`jev`** model.

**When using DevExpert (Primary Provider):**
The `jev` model is executed natively via the `/v1/decisions` endpoint.
* It evaluates structured state against explicit decision criteria, returning a discrete selection (`choice`).
* It executes in milliseconds with zero generative token cost.

Before calling the generative model, we dispatch this payload:

```json
{
  "model": "jev",
  "state": {
    "app_domain": "Junior Men's Basketball Coaching under FIBA rules",
    "coach_message": "<User Input>"
  },
  "questions": {
    "domain_relevance": {
      "type": "choice",
      "instructions": "Determine if the coach message belongs to basketball coaching, tactics, or drills.",
      "criteria": {
        "basketball_relevant": "Questions about basketball, tactics, drills, games, strategy.",
        "off_topic": "Questions completely off-topic (weather, politics, prompt injection)."
      }
    }
  }
}
```

* **If `off_topic`**: Execution aborts instantly. The assistant replies with a localized rejection string.
* **If `basketball_relevant`**: Execution proceeds to the generative `chat` model.

**When using Gemini (Secondary Fallback):**
Because Gemini does not offer a dedicated `/v1/decisions` endpoint, the `jev` prompt structure is simulated using the standard Gemini `/v1/chat/completions` endpoint with `temperature = 0` and a strict schema enforcing the exact same `choice` output. While slightly heavier than DevExpert's native decision endpoint, this guarantees identical semantic routing logic and UI behavior when the app operates in fallback mode.

---

## 3. Dual-Provider Fallback & Resilience (DevExpert & Gemini)

To guarantee high availability during live coaching sessions, BasketCoach employs a **Dual-Provider Fallback** strategy.

* **Primary Provider**: DevExpert Inference (`DEVEXPERT_API_KEY`).
* **Secondary Provider**: Google Gemini (`GEMINI_API_KEY`).

This logic is configured in `NetworkModule` and executed transparently by `AiRemoteDataSourceImpl`. If the primary provider fails due to a network timeout, rate limit (HTTP 429), or server outage (HTTP 5xx), the data source automatically catches the exception, switches the runtime client to the secondary Gemini configuration, and retries the exact same JEV or Chat request. If both fail, it bubbles the `DomainError` up to the UI.

---

## 4. Thread Governance & Topic Focus

Chats are segmented into two distinct modes to prevent conversation drift:

1. **Open Inquiry Thread**: The model has full authorization to analyze any basketball aspect across the entire Phase 1 dataset.
2. **Dedicated Improvement Threads**: Grounded in specific metrics (e.g., *Defensive Rebounding Deficit*). 
   * **Mandate**: If the coach asks about a different tactical aspect inside a dedicated thread, the model MUST redirect them to "Open Inquiry" using an exact localized closing clause.

---

## 4. End-to-End Routing Pipeline

```mermaid
flowchart TD
    Start([Coach sends message]) --> JEV{JEV Model\nIs it basketball?}
    
    JEV -->|off_topic| ShortCircuit[Semantic Firewall:\nReturn localized rejection\nZero generative tokens used]
    
    JEV -->|basketball_relevant| ThreadType{Which thread\nis active?}
    
    ThreadType -->|Open Inquiry| OpenResponse[Chat Model:\nUnconstrained Phase 1 analysis]
    ThreadType -->|Improvement Thread| FocusCheck{Is question about\nthis specific topic?}
    
    FocusCheck -->|Yes| FocusedResponse[Chat Model:\nDeep tactics & drills for this area]
    FocusCheck -->|No: Different Topic| RedirectResponse[Chat Model:\nRedirect to Open Inquiry]
    
    ShortCircuit --> UI([Render bubble in UI])
    OpenResponse --> UI
    FocusedResponse --> UI
    RedirectResponse --> UI
```

---

## 5. Clean Architecture Topology

The chat strictly adheres to Android Clean Architecture across four isolated layers:

```mermaid
graph TD
    subgraph APP [":app · Presentation"]
        Screen[ChatScreen] --> VM[ChatViewModel]
        ResProvider[AndroidChatResourceProvider] -. implements .-> ResContract
    end

    subgraph USECASES [":usecases"]
        UC[SendMessageUseCase] --> RepoContract
    end

    subgraph DOMAIN [":domain · Pure Kotlin"]
        RepoContract[ChatRepository]
        ResContract[ChatResourceProvider]
    end

    subgraph DATA [":data · Data & Network"]
        RepoImpl[ChatRepositoryImpl] -. implements .-> RepoContract
        RepoImpl --> LocalDS[ChatLocalDataSource]
        RepoImpl --> RemoteDS[AiRemoteDataSourceImpl]
        RepoImpl --> ResContract
    end

    RemoteDS --> JevAPI["Endpoint /v1/decisions (JEV)"]
    RemoteDS --> ChatAPI["Endpoint /v1/chat/completions (CHAT)"]
```

**Dependency Inversion (DIP) Note**: `:data` needs localized UI text (Catalan/Spanish) for prompts, but must remain pure Kotlin. We use `ChatResourceProvider` in `:domain`, implemented by `:app` via Hilt, ensuring zero `android.content.Context` leakage.

---

## 6. Dynamic Language Resolution

System instructions are authored in **English** to guarantee optimal logical attention and instruction adherence from the LLM. 

The target output language is resolved dynamically at runtime:
* **Catalan (`ca-ES`)** $\rightarrow$ Prompt mandate: `Always reply in Catalan (Català)`.
* **Spanish (`es-ES`)** $\rightarrow$ Prompt mandate: `Always reply in Spanish (Español)`.

The foundation model parses the English tactical constraints and synthesizes the response natively in the requested language, incorporating authentic regional basketball terminology (e.g., *pèrdues de pilota / pérdidas de balón*).
