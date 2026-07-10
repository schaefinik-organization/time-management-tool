package schaefinik.time.timeentry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    List<TimeEntry> findByEntryDateOrderByStartTimeAsc(LocalDate entryDate);
}
