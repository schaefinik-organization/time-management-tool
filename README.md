# time-management-tool

Ein Fullstack-Lern- und Praxisprojekt zur Zeiterfassung mit Java Spring Boot und Vue.js.

## Überblick

`time-management-tool` ist eine Webanwendung, mit der sich Nutzer anmelden, Projekte anlegen und Zeiten auf Projekte buchen können. Ziel des Projekts ist es, eine saubere und nachvollziehbare Fullstack-Anwendung aufzubauen, in der typische Anforderungen wie Authentifizierung, Projektverwaltung und Zeitbuchungen praktisch umgesetzt werden.

Das Projekt befindet sich aktuell in einer frühen Entwicklungsphase und deckt bereits die grundlegenden Kernfunktionen ab. Es wird laufend erweitert, verbessert und schrittweise technisch ausgebaut.

## Ziel des Projekts

Dieses Projekt dient dazu, gelernte Inhalte aus Backend- und Frontend-Entwicklung praktisch umzusetzen. Gleichzeitig wird es genutzt, um unterschiedliche Software-Patterns, Architekturen und Entwicklungsansätze in einer realistischen Webanwendung zu testen und nachvollziehbar weiterzuentwickeln.

## Aktueller Funktionsumfang

- Benutzeranmeldung und grundlegende Authentifizierung
- Verwaltung von Projekten
- Erfassung von Zeitbuchungen auf Projekte
- Beispielhafte Buchung von Zeiten, z. B. von 12:00 bis 14:00 Uhr
- Grundstruktur für Backend und Frontend
- Nachvollziehbare und strukturiert benannte Commits

## Tech-Stack

### Backend
- Java
- Spring Boot
- Spring Security
- Gradle

### Frontend
- Vue.js
- Vite

## Projektstatus

Das Projekt ist aktiv in Entwicklung. Der aktuelle Stand konzentriert sich bewusst auf die Core-Funktionen einer Zeiterfassungsanwendung. Weitere Features, strukturelle Verbesserungen und technische Verfeinerungen sind geplant.

## Entwicklungsansatz

Ein wichtiger Teil des Projekts ist die nachvollziehbare Entwicklung über klar strukturierte Commits. Änderungen werden schrittweise aufgebaut und thematisch benannt, damit die Weiterentwicklung transparent bleibt.

Beispiele aus dem Commit-Verlauf:

- `feature(TIME-01): refactor repo, added sample folder structure`
- `feature(TIME-01): added backend user and authentication and security`
- `feature(TIME-01): added initial frontend components and views`
- `feature(TIME-01): added frontend to the project with vite and vue`
- `feature(TIME-01): added project and timeEntry Controller, Services, Repositories`

Dadurch lässt sich die technische Entwicklung des Projekts gut nachvollziehen.

## Geplante Weiterentwicklung

- Erweiterung der Benutzer- und Rollenlogik
- Verbesserte Validierung und Fehlerbehandlung
- Optimierung der UI und Nutzerführung
- Erweiterung der Projekt- und Zeitverwaltungsfunktionen
- Tests und technische Absicherung
- Containerisierung und vereinfachtes lokales Setup

## Lokale Ausführung

Die genauen Startschritte können hier ergänzt werden, sobald das Setup vollständig dokumentiert ist.

Beispielhafte Struktur:

### Backend starten
```bash
./gradlew bootRun
