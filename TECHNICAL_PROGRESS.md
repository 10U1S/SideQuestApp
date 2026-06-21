# Technical Progress Report - SideQuest App (Stand: ab 04.05.2026)

## 1. Architektur & Navigation (Änderungen nach 04.05.26)
* **Datenbank-Integration (Room):** Die App nutzt nun eine Room-Datenbank zur persistenten Speicherung von Quests.
* **CitySelectionActivity:** Neuer Screen mit dynamischer Stadtauswahl via Dropdown-Menü (PopupMenu).
* **Automatisierte Städte-Erkennung:** Die App scannt den `assets`-Ordner nach CSV-Dateien und bietet diese automatisch als Städte an.
* **CSV-Importer:** Automatischer Import von Quest-Daten aus CSV-Dateien beim ersten Auswählen einer Stadt.
* **Workflow:** Start -> Stadtauswahl -> Zeitauswahl -> Quest-Details -> Karte -> Abschluss.

## 2. UI/UX Refinement
* **City Selection:** Design der Stadtauswahl im konsistenten Aero-Stil mit Glass-Pill Titeln.

## 3. Logik & Daten (Aktualisiert)
* **Data Source:** Umstellung von statischen Listen auf eine relationale Datenbank (Room).
* **Multi-City Support:** Unterstützung für mehrere Städte (derzeit Nürnberg und München vorkonfiguriert).
* **CSV-Struktur:** Quests werden über einfache Semikolon-getrennte Dateien definiert.

## 4. Known Issues & Roadmap
### Aktueller Stand (Known Issues)
* **Main Thread DB Access:** Derzeit wird der Datenbankzugriff der Einfachheit halber auf dem Main Thread erlaubt (`allowMainThreadQueries`), was bei sehr großen CSVs zu kurzen Rucklern führen kann.
* **CSV-Umlaut Handling:** Dateinamen und Pfade müssen präzise mit den Asset-Namen übereinstimmen.

### Nächste Meilensteine
* [ ] Live-User-Tracking auf der Karte.
* [ ] Geofencing ("Check-in" am Quest-Ziel).
* [ ] Persistenz via (Room-)Datenbank.
* [ ] Speichern von Quests,die User gefallen.


## 5. Testing & Quality Assurance
* **Unit Testing:** Implementierung von automatisierten JUnit 4 Tests für das `QuestRepository`.
* **Logic Validation:** Die Tests verifizieren die korrekte Filterung der Quests nach Zeitintervallen (Kurz, Mittel, Lang) und stellen sicher, dass keine unpassenden Aufgaben vorgeschlagen werden.
* **Status:** Test-Suite erfolgreich integriert und lauffähig.

## 6. AI Use Note
Dieses Dokument wurde unter Zuhilfenahme von KI-Assistenz erstellt, um den technischen Fortschritt und die durchgeführten Änderungen zusammenzufassen.
