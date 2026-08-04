package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.repository.ProjectRepository;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Order(2)
public class TestProjectSeeder implements CommandLineRunner {
	private final ProjectRepository projectRepository;

	@Override
	public void run(String... args) {
		if (projectRepository.count() == 0) {
			projectRepository.saveAll(List.of(
					ProjectModel.builder().name("Sport").description("Körperliche Betätigung").active(true).build(),
					ProjectModel.builder().name("Software").description("Projektentwicklung Code").active(true).build()
			));
		}
	}
}