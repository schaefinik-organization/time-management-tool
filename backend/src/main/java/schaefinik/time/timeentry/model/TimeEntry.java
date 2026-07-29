package schaefinik.time.timeentry.model;

import jakarta.persistence.*;
import lombok.*;
import schaefinik.time.project.model.Project;
import schaefinik.time.user.model.TimeUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "time_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private TimeUser user;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(nullable = false)
	private LocalDate entryDate;

	@Column(nullable = false)
	private LocalTime startTime;

	@Column(nullable = false)
	private LocalTime endTime;

	@Column(length = 500)
	private String note;

	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}
