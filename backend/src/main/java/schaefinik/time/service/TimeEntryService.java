package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.exception.InvalidTimeRangeException;
import schaefinik.time.exception.OverlappingTimeException;
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

	@Transactional
	public TimeEntryResponse createOrUpdateTimeEntry(TimeEntryRequest request) {
		TimeEntryModel entry = TimeEntryModel.builder()
				.project(projectService.getProject(request.projectId()))
				.user(userService.getCurrentUser())
				.startTime(request.startTime())
				.endTime(request.endTime())
				.note(request.note())
				.build();

		if (!entry.getStartTime().isBefore(entry.getEndTime())) {
			throw new InvalidTimeRangeException(TimeEntryProperties.TIME_ENTRY_END_TIME_BEFORE_START_TIME);
		}

		boolean hasOverlap = timeEntryRepository.existsOverlappingEntry(
				entry.getUser().getId(),
				entry.getStartTime(),
				entry.getEndTime(),
				entry.getId()
		);

		if (hasOverlap) {
			throw new OverlappingTimeException(TimeEntryProperties.TIME_ENTRY_OVERLAP);
		}

		return mapToResponse(timeEntryRepository.save(entry));
	}

	public void delete(Long id) {
		TimeEntryModel entry = getTimeEntry(id);

		timeEntryRepository.delete(entry);
	}

	public List<ReportDTO> getAggregatedReport(LocalDate start, LocalDate end) {
		return timeEntryRepository.getAggregatedReport(start, end);
	}

	private TimeEntryResponse mapToResponse(TimeEntryModel entry) {
		return new TimeEntryResponse(
				entry.getId(),
				entry.getProject().getId(),
				entry.getProject().getName(),
				entry.getStartTime(),
				entry.getEndTime(),
				entry.getNote());
	}
}
