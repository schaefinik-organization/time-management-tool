package schaefinik.time.timeentry;

import org.springframework.stereotype.Service;
import schaefinik.time.project.Project;
import schaefinik.time.project.ProjectRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final ProjectRepository projectRepository;

    public TimeEntryService(TimeEntryRepository timeEntryRepository,
            ProjectRepository projectRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.projectRepository = projectRepository;
    }

    public List<TimeEntryResponse> findByDate(LocalDate date) {
        return timeEntryRepository.findByEntryDateOrderByStartTimeAsc(date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TimeEntryResponse create(CreateTimeEntryRequest request) {
        validateTimeRange(request);

        Project project = findProject(request.projectId());

        TimeEntry entry = new TimeEntry();
        entry.setProject(project);
        entry.setEntryDate(request.entryDate());
        entry.setStartTime(request.startTime());
        entry.setEndTime(request.endTime());
        entry.setNote(request.note());

        TimeEntry saved = timeEntryRepository.save(entry);
        return mapToResponse(saved);
    }

    public TimeEntryResponse update(Long id, CreateTimeEntryRequest request) {
        validateTimeRange(request);

        TimeEntry entry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TimeEntry nicht gefunden: " + id));

        Project project = findProject(request.projectId());

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
