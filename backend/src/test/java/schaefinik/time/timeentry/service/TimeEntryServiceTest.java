package schaefinik.time.timeentry.service;

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
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.util.SecurityUtil;
import schaefinik.time.timeentry.exception.TimeEntryOverlapException;
import schaefinik.time.timeentry.model.TimeEntry;
import schaefinik.time.timeentry.properties.TimeEntryProperties;
import schaefinik.time.timeentry.repository.TimeEntryRepository;
import schaefinik.time.timeentry.requestData.TimeEntryRequest;
import schaefinik.time.timeentry.responseData.TimeEntryResponse;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TimeEntryServiceTest {

    @InjectMocks
    private TimeEntryService sut;

    @Mock
    private TimeEntryRepository timeEntryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TimeUserRepository userRepository;

}
