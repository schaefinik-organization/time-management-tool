package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import schaefinik.time.response.manager.TeamMemberReportDTO;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeEntryService {

	private final TimeEntryRepository timeEntryRepository;
	private final ProjectService projectService;
	private final UserService userService;
	private final DataMapper dataMapper;
	private final TeamReportMapper teamReportMapper;

	@Transactional(readOnly = true)
	public List<TimeEntryDTO> getMyTimeEntries() {
		TimeUserModel currentUser = userService.getCurrentUser();
		return timeEntryRepository.findByUserIdOrderByStartTimeDesc(currentUser.getId())
				.stream()
				.map(this::mapToDTO)
				.toList();
	}

	@Transactional
	public TimeEntryDTO createEntry(TimeEntryRequest request) {
		TimeUserModel currentUser = userService.getCurrentUser();
		ProjectModel project = projectService.getProject(request.getProjectId());
		verifyUserCanBookOnProject(currentUser, project);
		validateTimeRange(currentUser.getId(), request.getStartTime(), request.getEndTime(), null);
		TimeEntryModel entry = TimeEntryModel.builder()
				.user(currentUser)
				.project(project)
				.startTime(request.getStartTime())
				.endTime(request.getEndTime())
				.description(request.getDescription())
				.build();

		return mapToDTO(timeEntryRepository.save(entry));
	}

	@Transactional
	public TimeEntryDTO updateEntry(Long id, TimeEntryRequest request) {
		TimeEntryModel entry = getTimeEntry(id);
		TimeUserModel currentUser = userService.getCurrentUser();
		if (!entry.getUser().getId().equals(currentUser.getId())) {
			throw new AccessDeniedException("Du darfst nur deine eigenen Zeiten bearbeiten.");
		}
		ProjectModel project = projectService.getProject(request.getProjectId());
		verifyUserCanBookOnProject(currentUser, project);
		validateTimeRange(currentUser.getId(), request.getStartTime(), request.getEndTime(), entry.getId());
		entry.setProject(project);
		entry.setStartTime(request.getStartTime());
		entry.setEndTime(request.getEndTime());
		entry.setDescription(request.getDescription());

		return mapToDTO(timeEntryRepository.save(entry));
	}

	@Transactional
	public void deleteEntry(Long id) {
		TimeEntryModel entry = getTimeEntry(id);
		if (!entry.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new AccessDeniedException("Du darfst nur deine eigenen Zeiten löschen.");
		}

		timeEntryRepository.delete(entry);
	}

	//For Booking View
	@Transactional(readOnly = true)
	public List<TimeEntryDTO> getCurrentUserTimeEntriesForMonth(YearMonth month) {
		TimeUserModel currentUser = userService.getCurrentUser();
		LocalDateTime start = month.atDay(1).atStartOfDay();
		LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
		List<TimeEntryModel> entries = timeEntryRepository.findByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
				currentUser.getId(), start, end);

		return entries.stream().map(this::mapToDTO).toList();
	}

	@Transactional(readOnly = true)
	public List<TeamMemberReportDTO> getCurrentManagerTeamReport(YearMonth month) {
		TimeUserModel currentManager = userService.getCurrentUser();
		if (currentManager.getRole() == Role.ROLE_USER) {
			throw new AccessDeniedException("Normale User haben keinen Zugriff auf Team-Reports.");
		}
		if (month == null) {
			month = YearMonth.now(Clock.systemDefaultZone());
		}
		LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

		return teamReportMapper.mapToTeamMemberReports(
				timeEntryRepository.
						getFlatManagerReport(
								currentManager.getId(),
								startOfMonth,
								endOfMonth)
		);
	}

	protected TimeEntryModel getTimeEntry(Long id) {
		return timeEntryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Buchung mit ID " + id + " nicht gefunden"));
	}

	private void verifyUserCanBookOnProject(TimeUserModel user, ProjectModel project) {
		if (!project.isActive()) {
			throw new IllegalStateException("Das Projekt ist archiviert. Buchungen sind nicht möglich.");
		}

		boolean isManager = false;
		if (project.getManager() != null) {
			isManager = project.getManager().getId().equals(user.getId());
		}
		boolean isAssigned = project.getAssignedUsers().stream()
				.anyMatch(u -> u.getId().equals(user.getId()));

		if (!isManager && !isAssigned) {
			throw new AccessDeniedException("Du bist diesem Projekt nicht zugewiesen.");
		}
	}

	private void validateTimeRange(Long userId, LocalDateTime start, LocalDateTime end, Long currentEntryId) {
		if (!start.isBefore(end)) {
			throw new InvalidTimeRangeException("Die Startzeit muss vor der Endzeit liegen.");
		}

		if (timeEntryRepository.existsOverlappingEntry(userId, start, end, currentEntryId)) {
			throw new OverlappingTimeException("In diesem Zeitraum existiert bereits eine Buchung.");
		}
	}

	private TimeEntryDTO mapToDTO(TimeEntryModel timeEntryModel) {
		return dataMapper.toTimeEntryDto(timeEntryModel);
	}
}
