package schaefinik.time.project.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.project.model.Project;
import schaefinik.time.project.repository.ProjectRepository;
import schaefinik.time.project.requestData.ProjectRequest;
import schaefinik.time.project.responseData.ProjectResponse;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService sut;

    @Mock
    private ProjectRepository projectRepository;
}
