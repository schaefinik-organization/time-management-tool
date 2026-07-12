package schaefinik.time.timeentry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import schaefinik.time.project.Project;
import schaefinik.time.project.ProjectRepository;
import schaefinik.time.security.SecurityUtil;
import schaefinik.time.security.TimeUserPrincipal;
import schaefinik.time.user.TimeUser;
import schaefinik.time.user.TimeUserRepository;

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

    public TimeEntryResponse create(CreateTimeEntryRequest request) {
        validateTimeRange(request);
        checkForOverlap(request, null);

        TimeUserPrincipal principal = SecurityUtil.currentUser();
        TimeUser user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Project project = findProject(request.projectId());

        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
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

    public TimeEntryResponse update(Long id, CreateTimeEntryRequest request) {
        validateTimeRange(request);

        TimeEntry entry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TimeEntry nicht gefunden: " + id));

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
                .orElseThrow(() -> new IllegalArgumentException("TimeEntry nicht gefunden: " + id));

        timeEntryRepository.delete(entry);
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projekt nicht gefunden: " + projectId));
    }

    private void validateTimeRange(CreateTimeEntryRequest request) {
        if (request.startTime() == null || request.endTime() == null) {
            throw new IllegalArgumentException("Start- und Endzeit müssen gesetzt sein");
        }

        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("Endzeit muss nach der Startzeit liegen");
        }
    }

    private void checkForOverlap(CreateTimeEntryRequest request, Long currentEntryId) {
        List<TimeEntry> entriesForDay = timeEntryRepository.findByEntryDate(request.entryDate());

        boolean overlaps = entriesForDay.stream()
                .filter(existing -> currentEntryId == null || !existing.getId().equals(currentEntryId))
                .anyMatch(existing -> request.startTime().isBefore(existing.getEndTime()) &&
                        request.endTime().isAfter(existing.getStartTime()));

        if (overlaps) {
            throw new TimeEntryOverlapException(
                    "Der Zeiteintrag überschneidet sich mit einem bestehenden Eintrag am selben Tag");
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
