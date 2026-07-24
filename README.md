# ⏱️ Time Management Tool [ALPHA]

[![Project Status: Alpha](https://img.shields.io/badge/Status-Alpha-orange.svg)](#)
[![Java](https://img.shields.io/badge/Backend-Java%2017%2B-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Frontend-Vue.js%203-4fc08d)](https://vuejs.org/)

Ein Fullstack-Praxisprojekt zur Zeiterfassung. Mit diesem Tool können Nutzer Projekte verwalten und Arbeitszeiten präzise dokumentieren. Entwickelt als Lern- und Referenzprojekt für moderne Web-Architekturen.

---

## 📑 Inhaltsverzeichnis
- [Überblick & Features](#-überblick--features)
- [Architektur](#-architektur)
- [Tech-Stack](#-tech-stack)
- [Quick Start (Setup)](#-quick-start-setup)
- [Konfiguration](#-konfiguration)
- [API Dokumentation](#-api-dokumentation)
- [Benutzerhandbuch](#-benutzerhandbuch)
- [Fehlerbehebung (Troubleshooting)](#-fehlerbehebung)
- [Contributing](#-contributing)

---

## 🚀 Überblick & Features

### Kernfunktionen (Aktueller Stand)
- **Authentifizierung:** Sicherer Login-Bereich für Benutzer.
- **Projektverwaltung:** Erstellen und Verwalten von Kundenprojekten.
- **Zeiterfassung:** Buchung von Zeitintervallen (z.B. 09:00 - 17:00 Uhr) auf spezifische Projekte.
- **Dashboard:** Erste Übersicht über erfasste Zeiten.

### Geplante Features
- [ ] Rollenbasierte Zugriffskontrolle (Admin vs. User)
- [ ] Export-Funktion (PDF/CSV)
- [ ] Visualisierung der Arbeitszeiten (Charts)
- [ ] Containerisierung mit Docker

---

## 🏗 Architektur

Das Projekt folgt einer klassischen **Decoupled Architecture**:
- **Backend:** RESTful API mit Spring Boot.
- **Frontend:** Single Page Application (SPA) mit Vue.js & Vite.
- **Kommunikation:** JSON via HTTP, abgesichert durch Spring Security (JWT/Session-basiert).

---

## 🛠 Tech-Stack

| Komponente | Technologie |
| :--- | :--- |
| **Backend** | Java 17+, Spring Boot, Spring Security, Hibernate/JPA |
| **Frontend** | Vue.js 3, Vite, Axios, Tailwind CSS (optional) |
| **Build-Tools** | Gradle, NPM |
| **Datenbank** | H2 (Entwicklung) / PostgreSQL (geplant) |

---

## 💻 Quick Start (Setup)

Folge diesen Schritten, um das Projekt in unter 15 Minuten lokal zu starten.

### Voraussetzungen
- Java JDK 17 oder höher
- Node.js (v18+) & NPM
- Git

### 1. Repository klonen
```bash
git clone https://github.com/schaefinik/time-management-tool.git
cd time-management-tool
```

### 2. Backend starten
```bash
cd backend
./gradlew bootRun
```
*Das Backend ist standardmäßig unter `http://localhost:8080` erreichbar.*

### 3. Frontend starten
```bash
cd frontend
npm install
npm run dev
```
*Das Frontend ist unter `http://localhost:5173` erreichbar.*

---

## 🔐 Test-Zugangsdaten (Default Credentials)

Für Testzwecke sind initial folgende Benutzer angelegt (via `import.sql` oder Setup-Service):

| Rolle | Benutzername | Passwort |
| :--- | :--- | :--- |
| **Admin** | `admin@example.com` | `admin123` |
| **User** | `user@example.com` | `user123` |

---

## 📡 API Dokumentation

Die API-Endpunkte können nach dem Start des Backends über **Swagger UI** eingesehen werden:
👉 `http://localhost:8080/swagger-ui.html` (falls implementiert)

**Wichtige Endpunkte:**
- `POST /api/auth/login` - Authentifizierung
- `GET /api/projects` - Liste aller Projekte
- `POST /api/time-entries` - Neue Zeitbuchung erstellen

---

## 📘 Benutzerhandbuch

1. **Login:** Melde dich mit den oben genannten Test-Daten an.
2. **Projekt anlegen:** Navigiere zu "Projekte" und erstelle dein erstes Projekt (z.B. "Lernprojekt README").
3. **Zeit buchen:** Wähle das Projekt aus, gib Start- und Endzeit ein und speichere die Buchung.
4. **Auswertung:** In der Übersicht siehst du deine summierten Stunden.

---

## ⚙️ Konfiguration

Die Anwendung kann über Umgebungsvariablen oder die `application.properties` angepasst werden:

- `SERVER_PORT`: Standard `8080`
- `SPRING_DATASOURCE_URL`: Datenbank-Verbindung
- `VITE_API_BASE_URL`: Frontend-Konfiguration für die API-URL

---

## 🛠 Fehlerbehebung (Troubleshooting)

- **Port bereits belegt:** Falls Port 8080 belegt ist, ändere ihn in der `application.properties`.
- **CORS Fehler:** Stelle sicher, dass die Frontend-URL in den Backend-Security-Einstellungen erlaubt ist.
- **Node Modules:** Bei Fehlern im Frontend hilft oft ein `rm -rf node_modules && npm install`.

---

## 🤝 Contributing

Beiträge sind herzlich willkommen!
1. Forke das Projekt.
2. Erstelle einen Feature-Branch (`git checkout -b feature/AmazingFeature`).
3. Beachte das Commit-Format: `feature(TIME-XX): beschreibung`.
4. Erstelle einen Pull Request.

---

## 📄 Lizenz
Dieses Projekt ist für Lernzwecke gedacht. (Füge hier ggf. MIT oder Apache 2.0 hinzu).

---
*Erstellt mit ❤️ von [schaefinik](https://github.com/schaefinik)*

---
