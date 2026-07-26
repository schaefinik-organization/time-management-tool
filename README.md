# ⏱️ Time Management Tool [ALPHA]

[![Java](https://img.shields.io/badge/Backend-Java%2021-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot%203-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Frontend-Vue.js%203-4fc08d)](https://vuejs.org/)

Ein Fullstack-Praxisprojekt zur Zeiterfassung mit automatisierter Deployment-Pipeline.

---

## 🛠 Entwickler-Werkzeuge (Gradle Tasks)

In diesem Projekt dient Gradle als zentrale Schaltzentrale.

| Befehl | Beschreibung |
| :--- | :--- |
| `./gradlew devBuild` | Vollständiger Build (Frontend & Backend) für die lokale Entwicklung. |
| `./gradlew dbReset` | **Daten-Reset:** Stoppt Container, löscht alle DB-Inhalte und startet sauber neu. |
| `./gradlew deepClean` | Löscht alle Build-Artefakte inkl. `node_modules`. |
| `./deploy-manage.sh backup dev` | Erstellt ein Datenbank-Dump und sichert das aktuelle Deployment. |
| `./deploy-manage.sh rollback dev`| Stellt den letzten Backup-Zustand (DB) wieder her. |

---

## 🚀 Quick Start (Setup)

### 1. Repository & Infrastruktur
```bash
git clone https://github.com/schaefinik/time-management-tool.git
cd time-management-tool

# Datenbank starten
docker-compose up -d db
```

### 2. Backend & Frontend initialisieren
```bash
cd backend
./gradlew devBuild
./gradlew bootRun
```

### 3. Frontend Dev-Server (optional für HMR)
```bash
cd frontend
npm install
npm run dev
```

---

## 🔐 Test-Zugangsdaten
Nach dem ersten Start werden automatisch Testdaten über die Seeder-Klassen angelegt. Die Werte können über Umgebungsvariablen gesteuert werden.

| Rolle | Benutzername | Default Passwort | Env-Variable |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `Admin123!` | `ADMIN_PASSWORD` |
| **User** | `user` | `User123!` | `TESTUSER_PASSWORD` |

---

## 🏗 Deployment Management
Das Projekt verfügt über ein integriertes Backup-System vor jedem Deployment. Backups werden im Verzeichnis `/backups` gespeichert (automatisch ignoriert von Git).

**Manueller Rollback:**
```bash
./deploy-manage.sh rollback dev
```

---

*Erstellt mit ❤️ von [schaefinik](https://github.com/schaefinik)*
