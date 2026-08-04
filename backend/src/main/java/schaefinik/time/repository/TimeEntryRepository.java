package schaefinik.time.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.responseData.ReportDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntryModel, Long> {

	List<TimeEntryModel> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);

	@Query("SELECT COUNT(t) > 0 FROM TimeEntryModel t WHERE t.user.id = :userId " +
			"AND t.startTime < :endTime " +
			"AND t.endTime > :startTime " +
			"AND (:entryId IS NULL OR t.id != :entryId)")
	boolean existsOverlappingEntry(
			@Param("userId") Long userId,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime,
			@Param("entryId") Long entryId
	);

	@Query("""
			SELECT new ReportDTO(u.username, p.name, SUM(
			(EXTRACT(HOUR FROM t.endTime) * 60 + EXTRACT(MINUTE FROM t.endTime)) - 
			(EXTRACT(HOUR FROM t.startTime) * 60 + EXTRACT(MINUTE FROM t.startTime))
			))
			FROM TimeEntryModel t
			JOIN t.user u
			JOIN t.project p
			WHERE t.entryDate BETWEEN :start AND :end
			GROUP BY u.username, p.name
			""")
	List<ReportDTO> getAggregatedReport(LocalDate start, LocalDate end);
}
