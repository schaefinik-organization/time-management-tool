package schaefinik.time.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.response.UserProjectHoursDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntryModel, Long> {

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

	List<TimeEntryModel> findByUserIdOrderByStartTimeDesc(Long userId);

	List<TimeEntryModel> findByProjectIdOrderByStartTimeDesc(Long projectId);

	List<TimeEntryModel> findByProjectIdAndStartTimeBetweenOrderByStartTimeDesc(
			Long projectId,
			LocalDateTime start,
			LocalDateTime end
	);

	@Query("SELECT new schaefinik.time.response.UserProjectHoursDto(" +
			"u.id, u.username, SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, t.startTime, t.endTime))) " +
			"FROM TimeEntryModel t JOIN t.user u " +
			"WHERE t.project.id = :projectId " +
			"AND (:start IS NULL OR t.startTime >= :start) " +
			"AND (:end IS NULL OR t.startTime <= :end) " +
			"GROUP BY u.id, u.username " +
			"ORDER BY u.username ASC")
	List<UserProjectHoursDto> getAggregatedHoursPerUser(
			@Param("projectId") Long projectId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end
	);
}
