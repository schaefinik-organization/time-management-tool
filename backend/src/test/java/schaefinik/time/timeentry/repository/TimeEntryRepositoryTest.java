package schaefinik.time.timeentry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import schaefinik.time.timeentry.model.TimeEntry;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    List<TimeEntry> findByEntryDateOrderByStartTimeAsc(LocalDate entryDate);

    List<TimeEntry> findByEntryDate(LocalDate entryDate);

    List<TimeEntry> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);
}
