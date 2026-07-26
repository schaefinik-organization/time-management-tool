package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schaefinik.time.project.repository.ProjectRepository;
import schaefinik.time.user.repository.TimeUserRepository;
import org.springframework.beans.factory.annotation.Value;
import schaefinik.time.timeentry.model.TimeEntry;
import schaefinik.time.timeentry.repository.TimeEntryRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;

@Component
@RequiredArgsConstructor
@Order(4)
public class TestTimeEntrySeeder implements CommandLineRunner {
    private final TimeUserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TimeEntryRepository timeEntryRepository;

    @Value("${seed.test.firstJob:Joggen}")
    private String firstJob;

    @Value("${seed.test.secondJob:Coding}")
    private String secondJob;

    @Override
    public void run(String... args) {
        var testUser = userRepository.findByUsername("test").orElse(null);
        var firstProject = projectRepository.findByName("Sport").orElse(null);
        var secondProject = projectRepository.findByName("Software").orElse(null);

        if (testUser != null && firstProject != null && secondProject != null) {
            // Create time entries for the test user
            // Example: Create a time entry for the first job in the Sport project
            TimeEntry firstTimeEntry = TimeEntry.builder()
                    .user(testUser)
                    .project(firstProject)
                    .note(firstJob)
                    .entryDate(LocalDate.now()) // Example entry date
                    .startTime(LocalTime.now()) // Example start time
                    .endTime(LocalTime.now().plusMinutes(120)) // Example end time
                    .build();
            // Save the time entry to the database
            timeEntryRepository.save(firstTimeEntry);

            // Example: Create a time entry for the second job in the Software project
            TimeEntry secondTimeEntry = TimeEntry.builder()
                    .user(testUser)
                    .project(secondProject)
                    .note(secondJob) // Example note for the second job
                    .entryDate(LocalDate.now()) // Example entry date
                    .startTime(LocalTime.now()) // Example start time
                    .endTime(LocalTime.now().plusMinutes(90)) // Example end time
                    .build();
            // Save the time entry to the database
            timeEntryRepository.save(secondTimeEntry);
        }
    }
}
