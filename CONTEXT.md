# Domain Glossary

* **Coach**: The primary user of the application, managing the CE Pia Sabadell Junior Men's Interterritorial team.
* **Game**: A recorded basketball match containing aggregated team performance metrics.
* **Team Stats**: Statistical data representing the performance of CE Pia Sabadell in a specific Game (excludes individual player statistics for the MVP).
* **Opponent Stats**: Statistical data representing the performance of the opposing team in a specific Game.
* **Insight**: A proactive, AI-generated observation or recommendation based on the static dataset, highlighting areas of improvement or synergies.
* **Drill**: A practice exercise recommended by the AI to address specific team weaknesses.
* **Dataset**: A bundled static collection of 14 games from Phase 1 in JSON/CSV format.
* **Phase 1**: The initial stage of the season from which the 14-game historical dataset was gathered.
* **Phase 2**: The upcoming second stage of the season starting in January against new opponents. The primary coaching objective is to improve performance in this phase based on Phase 1 insights.
* **DevExpert API**: The cloud-based AI service used to process the dataset and generate insights and chat responses.

## Definitive Source Materials

All AI agents must consult the following reference materials when implementing data parsing, basketball rule logic, or AI integration strategies:

* **[`docs/general/basket_rules.pdf`](file:///C:/Projectes/AI/BasketCoach/docs/general/basket_rules.pdf)**: Authoritative rulebook for basketball logic, constraints, and definitions.
* **[`docs/general/dataset.json`](file:///C:/Projectes/AI/BasketCoach/docs/general/dataset.json)**: The canonical dataset structure and sample data for parsing and data modeling.
* **[`docs/general/gemini_notebook_info.md`](file:///C:/Projectes/AI/BasketCoach/docs/general/gemini_notebook_info.md)**: Definitive guidelines and context for AI integration strategies, prompting, and insights generation.
