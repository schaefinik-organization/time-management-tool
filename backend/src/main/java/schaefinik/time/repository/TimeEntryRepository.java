package schaefinik.time.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import schaefinik.time.model.TimeEntryModel;
import schaefinik.time.response.manager.FlatManagerReportDTO;

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

	List<TimeEntryModel> findByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
			Long userId,
			LocalDateTime start,
			LocalDateTime end
	);

	@Query("SELECT new schaefinik.time.response.manager.FlatManagerReportDTO(" +
			"u.id, u.username, p.id, p.name, " +
			"SUM((EXTRACT(EPOCH FROM t.endTime) - EXTRACT(EPOCH FROM t.startTime)) / 3600.0), " +
			"SUM(CASE WHEN t.billable = true THEN (EXTRACT(EPOCH FROM t.endTime) - EXTRACT(EPOCH FROM t.startTime)) / 3600.0 ELSE 0.0 END), " +
			"COUNT(t.id)) " +
			"FROM TimeEntryModel t " +
			"JOIN t.user u " +
			"JOIN t.project p " +
			"WHERE u.manager.id = :managerId " +
			"AND (cast(:start as timestamp) IS NULL OR t.startTime >= :start) " +
			"AND (cast(:end as timestamp) IS NULL OR t.startTime <= :end) " +
			"GROUP BY u.id, u.username, p.id, p.name " +
			"ORDER BY u.username ASC, p.name ASC")
	List<FlatManagerReportDTO> getFlatManagerReport(
			@Param("managerId") Long managerId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end
	);

	@Query("SELECT new schaefinik.time.response.manager.FlatManagerReportDTO(" +
			"u.id, u.username, p.id, p.name, " +
			"SUM((EXTRACT(EPOCH FROM t.endTime) - EXTRACT(EPOCH FROM t.startTime)) / 3600.0), " +
			"SUM(CASE WHEN t.billable = true THEN (EXTRACT(EPOCH FROM t.endTime) - EXTRACT(EPOCH FROM t.startTime)) / 3600.0 ELSE 0.0 END), " +
			"COUNT(t.id)) " +
			"FROM TimeEntryModel t " +
			"JOIN t.user u " +
			"JOIN t.project p " +
			"WHERE u.id = :userId " +
			"AND (cast(:start as timestamp) IS NULL OR t.startTime >= :start) " +
			"AND (cast(:end as timestamp) IS NULL OR t.startTime <= :end) " +
			"GROUP BY u.id, u.username, p.id, p.name " +
			"ORDER BY p.name ASC")
	List<FlatManagerReportDTO> getUserTeamReport(
			@Param("userId") Long userId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end
	);

}
