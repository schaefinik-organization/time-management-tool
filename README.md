# ⏱️ Time Management Tool [BETA]

[![Java](https://img.shields.io/badge/Backend-Java%2021-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot%203-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Frontend-Vue.js%203%20(Vite)-4fc08d)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Deployment-Docker%20(TrueNAS)-2496ED)](https://www.docker.com/)

Ein modernes Fullstack-B2B-Projekt zur Zeiterfassung, Projektverwaltung und finanziellen Auswertung, gebaut nach Best Practices für Enterprise-Anwendungen.

---

## 🎯 Status & Features (Was funktioniert?)

**✅ Implementiert & Funktionstüchtig:**
*   **Role-Based Access Control (RBAC):** Hierarchisches Rollensystem (Admin, Manager, User) im Backend (Spring Security) und Frontend (Vue Router Guards).
*   **Hierarchische User-Verwaltung:** Manager können eigene Teams verwalten, Admins können projektübergreifend Rechte verteilen.
*   **Erweitertes Projektmanagement:** Zuweisung von Mitarbeitern zu Projekten via Multi-Select-UI, Definition von Budgets und Stundensätzen.
*   **Dynamische Dashboards:** Reactive Chart.js Integration für Stunden- und Umsatz-Auswertungen (Toggle-Funktion).
*   **Blob-Exporte:** Generierung und sicherer Download von PDF- und Excel-Reports direkt aus dem Backend.
*   **Qualitätssicherung:** Unit-Testing im Frontend (Vitest) und Backend (JUnit), gekoppelt mit JaCoCo und SonarQube für statische Code-Analyse.

**🚧 In Arbeit (Work in Progress):**
*   Vollständige Automatisierung des Datenbank-Rollbacks in der CI/CD Pipeline.
*   Erweiterung der Testabdeckung auf Edge-Cases.

---

## 🚀 Quick Start (Lokales Setup)

### 1. Infrastruktur (Datenbank)
Das Projekt nutzt PostgreSQL. Starte die lokale Datenbank am besten über Docker:
```bash
git clone [https://github.com/schaefinik/time-management-tool.git](https://github.com/schaefinik/time-management-tool.git)
cd time-management-tool

# Startet die PostgreSQL-Datenbank im Hintergrund
docker-compose up -d db

```

### 2. Backend starten (Spring Boot)

Das Backend nutzt MapStruct für sauberes DTO-Mapping und generiert beim Build automatisch den Code.

```bash
cd backend
./gradlew build -x test # Baut das Projekt (inkl. MapStruct)
./gradlew bootRun       # Startet den Server auf Port 8080

```

### 3. Frontend Dev-Server (Vue/Vite mit Tailwind v4)

```bash
cd frontend
npm install
npm run dev             # Startet mit Hot-Module-Replacement (HMR)

```

---

## 🔐 Test-Zugangsdaten

Beim ersten Start der Datenbank werden über Seeder-Klassen automatisch Testdaten generiert.

| Rolle | Benutzername | Default Passwort | Features |
| --- | --- | --- | --- |
| **Admin** | `admin` | `Admin123!` | Voller Systemzugriff, globale Zuweisungen. |
| **Manager** | `manager` | `Manager123!` | Sieht Team-Reports, legt User & Projekte an. |
| **User** | `user` | `User123!` | Erfasst Zeiten, sieht eigene Auswertungen. |

---

## 🛠 Testing & CI/CD (Gradle & GitHub Actions)

Dieses Projekt nutzt Gradle, um Backend- und Frontend-Tests für die SonarQube-Analyse zu bündeln.

| Befehl | Beschreibung |
| --- | --- |
| `./gradlew test` | Führt alle JUnit Backend-Tests aus. |
| `./gradlew testFrontend` | Startet Vitest (`npm run test:unit`) im Frontend inkl. Coverage. |
| `./gradlew jacocoTestReport` | Generiert die XML-Testabdeckung für SonarQube. |
| `./gradlew sonar` | Sendet den Code & die Coverage an das SonarQube Dashboard. |

---

## 🐳 Docker Deployment (TrueNAS)

Das Projekt ist für den Betrieb in isolierten Docker-Containern (z. B. auf einem TrueNAS-Server) optimiert. Da das Spring Boot JAR-File als Container läuft, müssen die Datenbank-Verbindungen zwingend über **Umgebungsvariablen (Environment Variables)** im Container-Setup übergeben werden:

* `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<DB_IP_ODER_CONTAINER>:5432/timedb`
* `SPRING_DATASOURCE_USERNAME` = `<DB_USER>`
* `SPRING_DATASOURCE_PASSWORD` = `<DB_PASSWORD>`
* `JWT_SECRET` = `<DEIN_SICHERS_BASE64_SECRET>`

---

*Entwickelt von [schaefinik](https://github.com/schaefinik) – Aktiv auf der Suche nach neuen Projekt-Herausforderungen!*
