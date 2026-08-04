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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Order(4)
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
		if (timeEntryRepository.count() > 0) {
			return;
		}

		TimeUserModel testUser = userRepository.findByUsername("test").orElse(null);
		ProjectModel sportProject = projectRepository.findByName("Sport").orElse(null);
		ProjectModel softProject = projectRepository.findByName("Software").orElse(null);

		if (testUser != null && sportProject != null && softProject != null) {

			for (int i = 0; i < 365; i++) {
				LocalDate date = LocalDate.now().minusDays(i);

				LocalDateTime sportStart = LocalDateTime.of(date, LocalTime.of(7 + random.nextInt(3), random.nextInt(60)));
				LocalDateTime sportEnd = sportStart.plusMinutes(30 + random.nextInt(61));

				timeEntryRepository.save(TimeEntryModel.builder()
						.user(testUser)
						.project(sportProject)
						.note(firstJob)
						.startTime(sportStart)
						.endTime(sportEnd)
						.build());

				LocalDateTime softStart = LocalDateTime.of(date, LocalTime.of(13 + random.nextInt(3), random.nextInt(60)));
				LocalDateTime softEnd = softStart.plusMinutes(120 + random.nextInt(181));

				timeEntryRepository.save(TimeEntryModel.builder()
						.user(testUser)
						.project(softProject)
						.note(secondJob)
						.startTime(softStart)
						.endTime(softEnd)
						.build());
			}

			System.out.println("✅ Test-Zeiteinträge für die letzten 365 Tage generiert.");
		} else {
			System.out.println("⚠️ Seeding der Zeiteinträge übersprungen: User oder Projekte nicht gefunden.");
		}
	}
}