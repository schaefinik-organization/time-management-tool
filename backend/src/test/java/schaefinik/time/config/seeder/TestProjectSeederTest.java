package schaefinik.time.config.seeder;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import schaefinik.time.project.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
public class TestProjectSeederTest {

  @InjectMocks
  private TestProjectSeederTest sut;

  @Mock
  private ProjectRepository projectRepository;

}
