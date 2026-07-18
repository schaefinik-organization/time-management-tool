package schaefinik.time.config.seeder;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import schaefinik.time.project.model.Project;
import schaefinik.time.project.repository.ProjectRepository;

@Component
@RequiredArgsConstructor
public class ProjectSeeder implements CommandLineRunner {

  private final ProjectRepository projectRepository;

  @Override
  public void run(String... args) {
    if (projectRepository.count() == 0) {
      projectRepository.saveAll(
          List.of(
              Project.builder().name("root").description("Rootproject").active(false).build(),
              Project.builder().name("first").description("First project").active(true).build(),
              Project.builder().name("second").description("Second project").active(true).build()));
    }
  }
}
