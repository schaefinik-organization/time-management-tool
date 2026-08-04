package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.ProjectRepository;
import schaefinik.time.repository.TimeEntryRepository;
import schaefinik.time.repository.TimeUserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Order(4) // Wichtig: Muss nach User- und ProjectSeeder laufen
public class TestTimeEntrySeeder implements CommandLineRunner {

	private final TimeUserRepository userRepository;
	private final ProjectRepository projectRepository;
	private final TimeEntryRepository timeEntryRepository;
	private final Random random = new Random();

	@Value("${seed.test.firstJob:Joggen}")
	private String firstJob;

	@Value("${seed.test.secondJob:Coding}")
	private String secondJob;

	@Override
	public void run(String... args) {
		// Nur seeden, wenn noch keine Einträge existieren
		if (timeEntryRepository.count() > 0) {
			return;
		}

		TimeUserModel testUser = userRepository.findByUsername("test").orElse(null);
		// Falls dein ProjectSeeder andere Namen nutzt (z.B. "first", "second"),
		// musst du die Namen hier anpassen oder im ProjectSeeder auf "Sport" & "Software" ändern.
		ProjectModel sportProject = projectRepository.findByName("Sport").orElse(null);
		ProjectModel softProject = projectRepository.findByName("Software").orElse(null);

		if (testUser != null && sportProject != null && softProject != null) {

			// Generiere Daten für die letzten 7 Tage
			for (int i = 0; i < 7; i++) {
				LocalDate date = LocalDate.now().minusDays(i);

				// 1. Eintrag: Sport (Vormittags)
				// Startzeit zwischen 07:00 und 09:00
				LocalTime sportStart = LocalTime.of(7 + random.nextInt(3), random.nextInt(60));
				// Dauer zwischen 30 und 90 Minuten
				LocalTime sportEnd = sportStart.plusMinutes(30 + random.nextInt(61));

				timeEntryRepository.save(TimeEntryModel.builder()
						.user(testUser)
						.project(sportProject)
						.note(firstJob)
						.entryDate(date)
						.startTime(sportStart)
						.endTime(sportEnd)
						.build());

				// 2. Eintrag: Software (Nachmittags)
				// Startzeit zwischen 13:00 und 15:00
				LocalTime softStart = LocalTime.of(13 + random.nextInt(3), random.nextInt(60));
				// Dauer zwischen 120 und 300 Minuten (2-5 Stunden)
				LocalTime softEnd = softStart.plusMinutes(120 + random.nextInt(181));

				timeEntryRepository.save(TimeEntryModel.builder()
						.user(testUser)
						.project(softProject)
						.note(secondJob)
						.entryDate(date)
						.startTime(softStart)
						.endTime(softEnd)
						.build());
			}

			System.out.println("✅ Test-Zeiteinträge für die letzten 7 Tage generiert.");
		} else {
			System.out.println("⚠️ Seeding der Zeiteinträge übersprungen: User oder Projekte nicht gefunden.");
		}
	}
}