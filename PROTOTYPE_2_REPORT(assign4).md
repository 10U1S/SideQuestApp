# Prototype 2 Report - SideQuest App

## 1. Executive Summary: Second Prototype
The second prototype focuses on the transition from hardcoded mock data to a **dynamic, database-driven system**. It introduces a scalable architecture capable of handling multiple cities and a robust workflow from start to completion.

### Essential Features Implemented:
*   **Persistent Storage (Room DB):** Quests are no longer static but stored in a local SQLite database.
*   **Dynamic City Management:** An automated CSV importer that populates the database based on asset files.
*   **Expanded Content:** Real quest data for 14 major German cities (> 500k residents).
*   **Navigation Logic:** A complete, circular user journey.
*   **Aero-Retro UI:** Consistent visual language across all screens (Windows XP/Aero theme).

## 2. Central Workflow Status
The core loop is **fully working and reliable**:
1.  **Start:** Entry via `StartActivity`.
2.  **Location:** `CitySelectionActivity` uses a dynamic Dropdown (PopupMenu) to select a city.
3.  **Intensity:** `TimeSelectionActivity` offers three distinct time intervals.
4.  **Discovery:** `QuestActivity` retrieves a random, filtered quest from Room.
5.  **Navigation:** `MapActivity` visualizes the target coordinates via OpenStreetMap.
6.  **Achievement:** `CompletionActivity` rewards the user and redirects back to the start/city selection.

## 3. User Stories & Acceptance Criteria (Updated)

| User Story | Acceptance Criteria | Status |
| :--- | :--- | :--- |
| As a user, I want to choose my city. | Dropdown shows all available CSV cities. | ✅ DONE |
| As a user, I want quests for my time. | Filtering logic (Short, Medium, Long) is accurate. | ✅ DONE |
| As a user, I want to see where to go. | Map opens with a marker at real coordinates. | ✅ DONE |
| As a developer, I want easy content updates. | New cities can be added via CSV without code changes. | ✅ DONE |
| As a developer, I want a stable app. | App handles empty states and DB migrations. | ✅ DONE |

## 4. Test Evidence
### Systematic Logic Validation
Automated **JUnit 4** tests were implemented in `QuestRepositoryTest.java` to verify the core filtering logic.
*   **Test 1 (Short):** Ensures interval 0-60 min returns correct duration. -> **PASSED**
*   **Test 2 (Medium):** Ensures interval 61-240 min returns correct duration. -> **PASSED**
*   **Test 3 (Long):** Ensures interval > 240 min returns correct duration. -> **PASSED**
*   **Test 4 (Empty):** Verified that a null result returns the Fallback-Quest. -> **PASSED**

### Workflow Verification
*   Manual path testing from `Start` to `Completion` performed for cities: Nürnberg, München, Berlin.
*   Verified that "Back" buttons do not lead to invalid states (Activity Stack clearing).

## 5. Bug List & Fixes

| Issue | Root Cause | Fix |
| :--- | :--- | :--- |
| **App Crash on City Select** | Room DB schema changed (added categories) without version update. | Incremented DB version and added `fallbackToDestructiveMigration`. |
| **Logic Error: Short quests in "All Day"** | Filtering used `<=` instead of strict interval ranges. | Refined `getRandomQuest` logic in `QuestRepository`. |
| **Build Error: Adaptive Icon** | `<adaptive-icon>` used on SDK < 26. | Raised `minSdk` to 26 in `build.gradle.kts`. |
| **Constructor Mismatch** | `Quest` object creation in Fallback was missing `category` field. | Updated all `new Quest(...)` calls to match updated constructor. |

## 6. Usability Review (Short Pass)
*   **Observation:** The city selection with many buttons was cluttered.
*   **Fix:** Switched to a single "Stadt auswählen" button with a dynamic PopupMenu.
*   **Observation:** The high-detail Windows XP background made white/blue text hard to read.
*   **Fix:** Added a `soft_background` (50% white blur layer) and "Aero-Glow" shadows to all titles.
*   **Observation:** Quests were too easy to accidentally close.
*   **Fix:** Separated "Karte" and "Erledigt" buttons clearly.

## 7. AI Use Note
This prototype was developed with the support of AI assistance for:
*   Implementing the **Room Persistence Library** and CSV-to-DB migration logic.
*   Generating **280+ unique Quests** with real coordinates for German cities.
*   Creating custom **XML Drawables** for the Aero/Retro design language.
*   Drafting the automated **JUnit 4 test suite**.
