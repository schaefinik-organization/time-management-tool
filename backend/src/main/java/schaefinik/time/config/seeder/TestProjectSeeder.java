@Component
@RequiredArgsConstructor
@Order(2)
public class TestProjectSeeder implements CommandLineRunner {
    private final ProjectRepository projectRepository;

    @Override
    public void run(String... args) {
        if (projectRepository.count() == 0) {
            projectRepository.saveAll(List.of(
                Project.builder().name("Intern").description("Interne Aufgaben").active(true).build(),
                Project.builder().name("Kunde Alpha").description("Projekt für Kunde A").active(true).build()
            ));
        }
    }
}