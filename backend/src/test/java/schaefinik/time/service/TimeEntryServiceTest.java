package schaefinik.time.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import schaefinik.time.enums.Role;
import schaefinik.time.exception.InvalidTimeRangeException;
import schaefinik.time.exception.OverlappingTimeException;
import schaefinik.time.exception.ResourceNotFoundException;
import schaefinik.time.mapper.DataMapper;
import schaefinik.time.mapper.TeamReportMapper;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeEntryRepository;
import schaefinik.time.request.TimeEntryRequest;
import schaefinik.time.response.entry.TimeEntryDTO;
import schaefinik.time.response.manager.FlatManagerReportDTO;
import schaefinik.time.response.manager.TeamMemberReportDTO;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceTest implements WithAssertions {

	private static final Long CURRENT_USER_ID = 1L;
	private static final Long OTHER_USER_ID = 2L;
	private static final Long PROJECT_ID = 1L;
	private static final Long TIME_ENTRY_ID = 1L;
	private static final LocalDateTime START_TIME = LocalDateTime.now();
	private static final LocalDateTime END_TIME = LocalDateTime.now().plusMinutes(30);
	private static final String DESCRIPTION = "Description";

	@InjectMocks
	private TimeEntryService sut;

	@Mock
	private TimeEntryRepository timeEntryRepository;

	@Mock
	private ProjectService projectService;

	@Mock
	private UserService userService;

	@Mock
	private DataMapper dataMapper;

	@Mock
	private TimeUserModel currentUser, otherUser;

	@Mock
	private TimeEntryModel timeEntryModel;

	@Mock
	private TimeEntryDTO timeEntryDTO;

	@Mock
	private ProjectModel projectModel;

	@Mock
	private FlatManagerReportDTO flatManagerReportDTO;

	@Mock
	private TeamMemberReportDTO teamMemberReportDTO;

	@Mock
	private TeamReportMapper teamReportMapper;

	@Captor
	private ArgumentCaptor<TimeEntryModel> timeEntryCaptor;

	@Test
	void getMyTimeEntries_getTimeEntriesByCurrentUser() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryRepository.findByUserIdOrderByStartTimeDesc(CURRENT_USER_ID)).thenReturn(List.of(timeEntryModel));
		when(dataMapper.toTimeEntryDto(timeEntryModel)).thenReturn(timeEntryDTO);

		var result = sut.getMyTimeEntries();

		verify(userService).getCurrentUser();
		verify(timeEntryRepository).findByUserIdOrderByStartTimeDesc(CURRENT_USER_ID);
		verify(dataMapper).toTimeEntryDto(timeEntryModel);
		assertThat(result).isEqualTo(List.of(timeEntryDTO));
	}

	@Test
	void createEntry_currentUserAssigned() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getAssignedUsers()).thenReturn(Set.of(currentUser));
		TimeEntryRequest request = createTimeEntryRequest();

		sut.createEntry(request);

		verify(userService).getCurrentUser();
		verify(projectService).getProject(PROJECT_ID);
		verify(timeEntryRepository).save(timeEntryCaptor.capture());
		TimeEntryModel savedEntry = timeEntryCaptor.getValue();
		assertThat(savedEntry).isNotNull();
		assertThat(savedEntry.getProject()).isEqualTo(projectModel);
		assertThat(savedEntry.getUser()).isEqualTo(currentUser);
		assertThat(savedEntry.getStartTime()).isEqualTo(request.getStartTime());
		assertThat(savedEntry.getEndTime()).isEqualTo(request.getEndTime());
		assertThat(savedEntry.getDescription()).isEqualTo(request.getDescription());
	}

	@Test
	void createEntry_currentUserManager() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getManager()).thenReturn(currentUser);
		TimeEntryRequest request = createTimeEntryRequest();

		sut.createEntry(request);

		verify(userService).getCurrentUser();
		verify(projectService).getProject(PROJECT_ID);
		verify(timeEntryRepository).save(timeEntryCaptor.capture());
		TimeEntryModel savedEntry = timeEntryCaptor.getValue();
		assertThat(savedEntry).isNotNull();
		assertThat(savedEntry.getProject()).isEqualTo(projectModel);
		assertThat(savedEntry.getUser()).isEqualTo(currentUser);
		assertThat(savedEntry.getStartTime()).isEqualTo(request.getStartTime());
		assertThat(savedEntry.getEndTime()).isEqualTo(request.getEndTime());
		assertThat(savedEntry.getDescription()).isEqualTo(request.getDescription());
	}

	@Test
	void createEntry_projectInactive_shouldThrowIllegalStateException() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(false);
		TimeEntryRequest request = createTimeEntryRequest();

		assertThatThrownBy(() -> sut.createEntry(request))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Das Projekt ist archiviert. Buchungen sind nicht möglich.");
	}

	@Test
	void createEntry_userInvalidForBooking_shouldThrowAccessDeniedException() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		TimeEntryRequest request = createTimeEntryRequest();

		assertThatThrownBy(() -> sut.createEntry(request))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessage("Du bist diesem Projekt nicht zugewiesen.");
	}

	@Test
	void createEntry_timeRangeInvalid_shouldThrowInvalidTimeRangeException() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getManager()).thenReturn(currentUser);
		TimeEntryRequest request = createTimeEntryRequest();
		request.setStartTime(request.getEndTime().plusMinutes(1));

		assertThatThrownBy(() -> sut.createEntry(request))
				.isInstanceOf(InvalidTimeRangeException.class)
				.hasMessage("Die Startzeit muss vor der Endzeit liegen.");
	}

	@Test
	void createEntry_timeRangeOverlappingEntry_shouldThrowOverlappingTimeException() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getManager()).thenReturn(currentUser);
		TimeEntryRequest request = createTimeEntryRequest();
		when(timeEntryRepository.existsOverlappingEntry(CURRENT_USER_ID, START_TIME, END_TIME, null)).thenReturn(true);

		assertThatThrownBy(() -> sut.createEntry(request))
				.isInstanceOf(OverlappingTimeException.class)
				.hasMessage("In diesem Zeitraum existiert bereits eine Buchung.");
	}

	@Test
	void updateEntry_shouldThrowResourceNotFoundException() {
		TimeEntryRequest request = createTimeEntryRequest();

		assertThatThrownBy(() -> sut.updateEntry(TIME_ENTRY_ID, request))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Buchung mit ID " + TIME_ENTRY_ID + " nicht gefunden");
	}

	@Test
	void updateEntry_shouldThrowAccessDeniedException() {
		TimeEntryRequest request = createTimeEntryRequest();
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryModel.getUser()).thenReturn(otherUser);
		when(otherUser.getId()).thenReturn(OTHER_USER_ID);

		assertThatThrownBy(() -> sut.updateEntry(TIME_ENTRY_ID, request))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessage("Du darfst nur deine eigenen Zeiten bearbeiten.");
	}

	@Test
	void updateEntry_projectInactive_shouldThrowIllegalStateException() {
		TimeEntryRequest request = createTimeEntryRequest();
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryModel.getUser()).thenReturn(currentUser);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(false);

		assertThatThrownBy(() -> sut.updateEntry(TIME_ENTRY_ID, request))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Das Projekt ist archiviert. Buchungen sind nicht möglich.");
	}

	@Test
	void updateEntry_userInvalidForBooking_shouldThrowAccessDeniedException() {
		TimeEntryRequest request = createTimeEntryRequest();
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryModel.getUser()).thenReturn(currentUser);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getAssignedUsers()).thenReturn(Collections.emptySet());

		assertThatThrownBy(() -> sut.updateEntry(TIME_ENTRY_ID, request))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessage("Du bist diesem Projekt nicht zugewiesen.");
	}


	@Test
	void updateEntry_timeRangeInvalid_shouldThrowInvalidTimeRangeException() {
		TimeEntryRequest request = createTimeEntryRequest();
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryModel.getUser()).thenReturn(currentUser);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getAssignedUsers()).thenReturn(Set.of(currentUser));
		request.setStartTime(request.getEndTime().plusMinutes(1));

		assertThatThrownBy(() -> sut.updateEntry(TIME_ENTRY_ID, request))
				.isInstanceOf(InvalidTimeRangeException.class)
				.hasMessage("Die Startzeit muss vor der Endzeit liegen.");
	}


	@Test
	void updateEntry_timeRangeOverlappingEntry_shouldThrowOverlappingTimeException() {
		TimeEntryRequest request = createTimeEntryRequest();
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryModel.getUser()).thenReturn(currentUser);
		when(timeEntryModel.getId()).thenReturn(TIME_ENTRY_ID);
		when(projectService.getProject(PROJECT_ID)).thenReturn(projectModel);
		when(projectModel.isActive()).thenReturn(true);
		when(projectModel.getAssignedUsers()).thenReturn(Set.of(currentUser));
		when(timeEntryRepository.existsOverlappingEntry(CURRENT_USER_ID, START_TIME, END_TIME, TIME_ENTRY_ID)).thenReturn(true);

		assertThatThrownBy(() -> sut.updateEntry(TIME_ENTRY_ID, request))
				.isInstanceOf(OverlappingTimeException.class)
				.hasMessage("In diesem Zeitraum existiert bereits eine Buchung.");
	}

	@Test
	void deleteEntry_notCurrentUser_shouldThrowAccessDeniedException() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(timeEntryModel.getUser()).thenReturn(otherUser);
		when(otherUser.getId()).thenReturn(OTHER_USER_ID);

		assertThatThrownBy(() -> sut.deleteEntry(TIME_ENTRY_ID))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessage("Du darfst nur deine eigenen Zeiten löschen.");
	}

	@Test
	void deleteEntry_currentUser() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(timeEntryRepository.findById(TIME_ENTRY_ID)).thenReturn(Optional.of(timeEntryModel));
		when(timeEntryModel.getUser()).thenReturn(currentUser);

		sut.deleteEntry(TIME_ENTRY_ID);

		verify(timeEntryRepository).delete(timeEntryModel);
	}

	@Test
	void getCurrentUserTimeEntriesForMonth_shouldReturnCurrentUserTimeEntry() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		YearMonth month = YearMonth.now();
		LocalDateTime start = month.atDay(1).atStartOfDay();
		LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
		when(timeEntryRepository.findByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
				CURRENT_USER_ID, start, end)).thenReturn(List.of(timeEntryModel));
		when(dataMapper.toTimeEntryDto(timeEntryModel)).thenReturn(timeEntryDTO);

		var result = sut.getCurrentUserTimeEntriesForMonth(month);

		verify(userService).getCurrentUser();
		verify(timeEntryRepository).findByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
				CURRENT_USER_ID, start, end);
		verify(dataMapper).toTimeEntryDto(timeEntryModel);
		assertThat(result).isEqualTo(List.of(timeEntryDTO));
	}

	@Test
	void getCurrentManagerTeamReport_roleUser_shouldThrowAccessDeniedException() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getRole()).thenReturn(Role.ROLE_USER);

		assertThatThrownBy(() -> sut.getCurrentManagerTeamReport(null))
				.isInstanceOf(AccessDeniedException.class)
				.hasMessage("Normale User haben keinen Zugriff auf Team-Reports.");
	}

	@Test
	void getCurrentManagerTeamReport_roleManagerOrHigher_getTeamReportDTO() {
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getRole()).thenReturn(Role.ROLE_MANAGER);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		YearMonth month = YearMonth.now(Clock.systemDefaultZone());
		LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);
		when(timeEntryRepository.getFlatManagerReport(CURRENT_USER_ID,
				startOfMonth,
				endOfMonth)).thenReturn(List.of(flatManagerReportDTO));
		when(teamReportMapper.mapToTeamMemberReports(List.of(flatManagerReportDTO))).thenReturn(List.of(teamMemberReportDTO));
		var result = sut.getCurrentManagerTeamReport(null);


		verify(timeEntryRepository).getFlatManagerReport(CURRENT_USER_ID, startOfMonth, endOfMonth);
		verify(teamReportMapper).mapToTeamMemberReports(List.of(flatManagerReportDTO));
		assertThat(result).isEqualTo(List.of(teamMemberReportDTO));
	}

	private TimeEntryRequest createTimeEntryRequest() {
		TimeEntryRequest request = new TimeEntryRequest();
		request.setProjectId(PROJECT_ID);
		request.setStartTime(START_TIME);
		request.setEndTime(END_TIME);
		request.setDescription(DESCRIPTION);
		return request;
	}

}