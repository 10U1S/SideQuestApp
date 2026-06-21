# Projektdokumentation - SideQuest App (Final Version)

## 1. Final Requirements Document (Anforderungen)
Die SideQuest App ist ein interaktives Reise-Tool zur spielerischen Erkundung von Städten weltweit.

*   **Globale Stadtauswahl:** Unterstützung von über 80 Städten (DACH, Europa, Weltweit) mit Kategorisierung und Flaggen-Emojis.
*   **Intelligentes GPS-Matching:** Automatische Erkennung von Städten "In deiner Nähe" basierend auf dem in den Einstellungen gewählten Radius.
*   **Quest-Logik:** Zufällige Zuweisung von Aufgaben basierend auf Stadt und Zeitbudget (60m, 240m, Ganztag).
*   **Aero-Glas Design:** Durchgängiges UI-Konzept mit Milchglas-Effekten, weichen Animationen und Parallax-Hintergründen.
*   **Echtzeit-Kontext:** Integration von Wetterdaten zur Warnung vor unpassenden Outdoor-Aktivitäten.
*   **Digitales Sammelalbum (Logbuch):** Speicherung von Quest-Erfolgen als personalisierte Polaroids mit handgeschriebenen Details (Titel, Datum) und Rarity-Glow-Effekten.
*   **Hybrid-Cloud-Infrastruktur:** Synchronisation zwischen lokaler SQLite (Room) und globaler Cloud (Firebase Firestore) für Offline-Verfügbarkeit und weltweiten Datenzugriff.

## 2. Design und Architektur Summary
Die App basiert auf einer robusten, schichtbasierten Android-Architektur (Java/Kotlin).

*   **Zentrale Steuerung (`BaseActivity`):** Verwaltet globale UI-Elemente, weiße Toolbar-Navigation und weiche Screen-Übergänge (Transitions).
*   **Datenmanagement:** 
    *   **Room:** Lokaler Cache für hohe Performance und Offline-Betrieb.
    *   **Firebase Firestore:** Master-Datenquelle zur dynamischen Skalierung neuer Städte ohne App-Update.
*   **Bild-Engine (Glide):** Sorgt für ein butterweiches Scrollen im Logbuch durch asynchrones Laden und intelligentes Memory-Management.
*   **UI-Techniken:** Komplexe `Layer-Lists` für Glas-Effekte und dynamische `ObjectAnimator` für haptisches Feedback bei Buttons.

## 3. Final Test Report (Abschluss-Testbericht)
*   **Unit Tests:** Erfolgreiche Validierung der Sortier- und Filterlogik im `QuestRepository` und `CitySelectionActivity` (Distanzberechnungen).
*   **Stresstest (Logbuch):** Prüfung der Performance bei >50 hochauflösenden Polaroids. Ergebnis: Stabil bei 60 FPS dank Glide-Caching.
*   **GPS-Feldtest:** Verifizierung der "In deiner Nähe"-Funktion unter realen Bedingungen (Radius-Umschaltung Settings -> Sofort-Update Stadtauswahl).
*   **Daten-Integrität:** Bereinigung aller 80+ CSV-Dateien von Syntaxfehlern (Anführungszeichen) und Verifizierung der Firebase-Batches.

## 4. Release Notes / Change Log
*   **v1.0:** Basis-Konzept (München/Nürnberg), CSV-Import, Room-Anbindung.
*   **v1.1:** Aero-Glas UI Redesign, Animationen, Wetter-Integration.
*   **v1.2:** Firebase Cloud-Sync, Logbuch-System mit Polaroid-Kamera-Funktion.
*   **v1.3:** Globale Expansion (80+ Städte), GPS-Radius-Feature, Performance-Optimierung (Glide), "Permanent Marker" Design.

## 5. Known Limitations (Bekannte Einschränkungen)
Ein ehrliches Projekt dokumentiert auch seine Schwachstellen:
*   **GPS-Abhängigkeit:** In geschlossenen Räumen kann der "In deiner Nähe"-Filter verzögert reagieren, bis ein erster Satelliten-Fix erfolgt.
*   **Initialer Daten-Load:** Beim ersten Öffnen einer neuen Stadt ist kurzzeitig Internet für den Firebase-Sync erforderlich (Fallback auf lokale Assets ist vorhanden, aber statisch).
*   **Wetter-API:** Begrenzte Freikontingente des OpenWeatherMap-Keys können bei extrem hohen Nutzerzahlen zu Ladefehlern führen.
*   **Hardware:** Die Glow-Effekte und Parallax-Hintergründe erfordern eine GPU mit moderner Shader-Unterstützung (Android 9+ empfohlen).

## 6. Final AI Use Reflection
Diese App wurde in enger Zusammenarbeit zwischen dem Entwickler und einer KI-Assistenz entwickelt. 
*   **Reflexion:** Die KI wurde als "Pair Programmer" für die Architektur-Struktur, das Debugging komplexer CSV-Syntaxfehler und die Implementierung der UI-Animationen genutzt. Die strategische Ausrichtung (Expansions-Plan, Polaroid-Idee) und das visuelle Feingefühl (Aero-Look) stammen vom menschlichen Entwickler. Diese Symbiose ermöglichte eine Entwicklungsgeschwindigkeit, die für eine Einzelperson in dieser Qualität sonst kaum erreichbar gewesen wäre.

---
*Abschlussdokumentation Stand: 02.06.2024*
