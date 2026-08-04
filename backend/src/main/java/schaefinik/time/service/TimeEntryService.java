package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.exception.TimeEntryOverlapException;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.properties.TimeEntryProperties;
import schaefinik.time.repository.TimeEntryRepository;
import schaefinik.time.requestData.TimeEntryRequest;
import schaefinik.time.responseData.ReportDTO;
import schaefinik.time.responseData.TimeEntryResponse;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeEntryService {

	private final TimeEntryRepository timeEntryRepository;
	private final ProjectService projectService;
	private final UserService userService;

	public TimeEntryModel getTimeEntry(Long id) {
		return timeEntryRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_NOT_FOUND + id));
	}

	public List<TimeEntryResponse> findByDateAndCurrentUser(LocalDate date) {
		TimeUserModel currentUser = userService.getCurrentUser();
		return timeEntryRepository.findByUserIdAndEntryDate(currentUser.getId(), date)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	public TimeEntryResponse create(TimeEntryRequest request) {
		validateTimeRange(request);
		checkForOverlap(request, null);

		TimeUserModel user = userService.getCurrentUser();
		ProjectModel project = projectService.getProject(request.projectId());

		if (!request.endTime().isAfter(request.startTime())) {
			throw new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_END_TIME_BEFORE_START_TIME);
		}

		TimeEntryModel entry = TimeEntryModel.builder()
				.user(user)
				.project(project)
				.entryDate(request.entryDate())
				.startTime(request.startTime())
				.endTime(request.endTime())
				.note(request.note())
				.build();

		TimeEntryModel saved = timeEntryRepository.save(entry);

		return mapToResponse(saved);
	}

	public TimeEntryResponse update(Long id, TimeEntryRequest request) {
		validateTimeRange(request);

		TimeEntryModel entry = getTimeEntry(id);
		ProjectModel project = projectService.getProject(request.projectId());
		checkForOverlap(request, id);

		entry.setProject(project);
		entry.setEntryDate(request.entryDate());
		entry.setStartTime(request.startTime());
		entry.setEndTime(request.endTime());
		entry.setNote(request.note());

		TimeEntryModel updated = timeEntryRepository.save(entry);
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		TimeEntryModel entry = getTimeEntry(id);

		timeEntryRepository.delete(entry);
	}

	public List<ReportDTO> getAggregatedReport(LocalDate start, LocalDate end) {
		return timeEntryRepository.getAggregatedReport(start, end);
	}

	private void validateTimeRange(TimeEntryRequest request) {
		if (request.startTime() == null) {
			throw new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_START_TIME_NULL);
		}

		if (!request.endTime().isAfter(request.startTime())) {
			throw new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_END_TIME_BEFORE_START_TIME);
		}
	}

	private void checkForOverlap(TimeEntryRequest request, Long currentEntryId) {
		List<TimeEntryModel> entriesForDay = timeEntryRepository.findByEntryDate(request.entryDate());
		
		boolean overlaps = entriesForDay.stream()
				.filter(existing -> !existing.getId().equals(currentEntryId))
				.anyMatch(existing ->
						request.startTime().isBefore(existing.getEndTime()) &&
								request.endTime().isAfter(existing.getStartTime()));

		if (overlaps) {
			throw new TimeEntryOverlapException(
					TimeEntryProperties.TIME_ENTRY_OVERLAP);
		}
	}

	private TimeEntryResponse mapToResponse(TimeEntryModel entry) {
		return new TimeEntryResponse(
				entry.getId(),
				entry.getProject().getId(),
				entry.getProject().getName(),
				entry.getEntryDate(),
				entry.getStartTime(),
				entry.getEndTime(),
				entry.getNote());
	}
}
