# Risks and Open Questions

## Technical Risks
* **Context Window Limits**: Passing 14 games of statistical data to the DevExpert API in a single prompt might exceed context limits or increase latency. *Mitigation: Summarize or format the data efficiently before sending.*
* **AI Hallucinations**: The DevExpert API might invent statistics or suggest irrelevant drills. *Mitigation: Strictly prompt the AI to only use the provided dataset and focus on FIBA/European basketball rules and context.*
* **DevExpert API Expiration**: The API is available for free for one month. *Mitigation: Ensure the AI repository/data source pattern is abstracted so it can be easily swapped to a direct Gemini/OpenAI integration later if needed.*

## Open Questions (Implementation-time)
* **Dataset Format**: What is the exact schema of the 14-game dataset (JSON vs CSV, exact statistical fields)?
* **API Key Management**: How will the DevExpert API key be securely provided to the app without exposing it in public version control?

## Open Questions (Later)
* **Wireframes**: The user will provide custom wireframes for the tablet UI later. The UI implementation must wait for or adapt to these designs.