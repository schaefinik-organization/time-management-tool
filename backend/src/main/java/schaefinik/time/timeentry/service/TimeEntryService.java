package schaefinik.time.timeentry.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
import schaefinik.time.report.responseData.ReportDTO;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final ProjectRepository projectRepository;
    private final TimeUserRepository userRepository;

    public List<TimeEntryResponse> findByDate(LocalDate date) {
        return timeEntryRepository.findByEntryDateOrderByStartTimeAsc(date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TimeEntryResponse create(TimeEntryRequest request) {
        validateTimeRange(request);
        checkForOverlap(request, null);

        TimeUserPrincipal principal = SecurityUtil.currentUser();
        TimeUser user = findUser(principal.getId());
        Project project = findProject(request.projectId());

        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_END_TIME_BEFORE_START_TIME);
        }

        TimeEntry entry = TimeEntry.builder()
                .user(user)
                .project(project)
                .entryDate(request.entryDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .note(request.note())
                .build();

        TimeEntry saved = timeEntryRepository.save(entry);

        return mapToResponse(saved);
    }

    public TimeEntryResponse update(Long id, TimeEntryRequest request) {
        validateTimeRange(request);

        TimeEntry entry = findTimeEntry(id);
        Project project = findProject(request.projectId());
        checkForOverlap(request, id);

        entry.setProject(project);
        entry.setEntryDate(request.entryDate());
        entry.setStartTime(request.startTime());
        entry.setEndTime(request.endTime());
        entry.setNote(request.note());

        TimeEntry updated = timeEntryRepository.save(entry);
        return mapToResponse(updated);
    }

    public void delete(Long id) {
        TimeEntry entry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_NOT_FOUND + id));

        timeEntryRepository.delete(entry);
    }

    public List<ReportDTO> getAggregatedReport(LocalDate start, LocalDate end) {
        return timeEntryRepository.getAggregatedReport(start, end);
    }

    private TimeEntry findTimeEntry(Long id) {
        return timeEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_NOT_FOUND + id));
    }

    private TimeUser findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TimeEntryProperties.TIME_ENTRY_USER_NOT_FOUND + id));
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException(
                        TimeEntryProperties.TIME_ENTRY_PROJECT_NOT_FOUND + projectId));
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
        List<TimeEntry> entriesForDay = timeEntryRepository.findByEntryDate(request.entryDate());

        boolean overlaps = entriesForDay.stream()
                .filter(existing -> currentEntryId == null || !existing.getId().equals(currentEntryId))
                .anyMatch(existing -> request.startTime().isBefore(existing.getEndTime()) &&
                        request.endTime().isAfter(existing.getStartTime()));

        if (overlaps) {
            throw new TimeEntryOverlapException(
                    TimeEntryProperties.TIME_ENTRY_OVERLAP);
        }
    }

    private TimeEntryResponse mapToResponse(TimeEntry entry) {
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
