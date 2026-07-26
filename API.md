#### 🔐 Authentifizierung
Die API nutzt JWT oder Session-Cookies. Bei JWT muss der Header `Authorization: Bearer <token>` mitgeschickt werden.

| Methode | Pfad | Beschreibung | Rollen |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Login & Erhalt des Tokens | Alle |
| `GET` | `/api/auth/me` | Details zum aktuell eingeloggten User | Alle |
| `POST` | `/api/users/change-password` | Passwort des eigenen Kontos ändern | Alle |

#### 📁 Projektverwaltung
Verwaltung von Kundenprojekten, auf die Zeiten gebucht werden.

| Methode | Pfad | Beschreibung | Beispiel Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/projects` | Liste aller aktiven Projekte | - |
| `POST` | `/api/projects` | Neues Projekt erstellen | `{"name": "Interne IT", "customer": "Firma XY"}` |
| `PUT` | `/api/projects/{id}` | Projektdaten aktualisieren | `{"name": "Neuer Name"}` |
| `DELETE` | `/api/projects/{id}` | Projekt löschen | - |

#### ⏱️ Zeiterfassung
Buchungen von Arbeitszeiten.

| Methode | Pfad | Parameter | Beschreibung |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/time-entries` | `date=YYYY-MM-DD` | Einträge für einen spezifischen Tag |
| `POST` | `/api/time-entries` | - | Neuen Eintrag erstellen (Start, Ende, ProjektId) |
| `PUT` | `/api/time-entries/{id}` | - | Eintrag korrigieren |
| `DELETE` | `/api/time-entries/{id}` | - | Eintrag entfernen |

---