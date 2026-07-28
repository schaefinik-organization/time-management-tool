package schaefinik.time.timeentry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schaefinik.time.timeentry.model.TimeEntry;
import org.springframework.data.jpa.repository.Query;
import schaefinik.time.report.responseData.ReportDTO;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    List<TimeEntry> findByEntryDateOrderByStartTimeAsc(LocalDate entryDate);

    List<TimeEntry> findByEntryDate(LocalDate entryDate);

    List<TimeEntry> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);

    // In TimeEntryRepository.java
    @Query("""
        SELECT new schaefinik.time.report.responseData.ReportDTO(u.username, p.name, SUM(
        (EXTRACT(HOUR FROM t.endTime) * 60 + EXTRACT(MINUTE FROM t.endTime)) - 
        (EXTRACT(HOUR FROM t.startTime) * 60 + EXTRACT(MINUTE FROM t.startTime))
        ))
        FROM TimeEntry t
        JOIN t.user u
        JOIN t.project p
        WHERE t.entryDate BETWEEN :start AND :end
        GROUP BY u.username, p.name
        """)
    List<ReportDTO> getAggregatedReport(LocalDate start, LocalDate end);
}
