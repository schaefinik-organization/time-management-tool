package schaefinik.time.model;

import jakarta.persistence.*;
import lombok.*;
import schaefinik.time.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(length = 500)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id", nullable = false)
	private TimeUserModel manager;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "project_users",
			joinColumns = @JoinColumn(name = "project_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@Builder.Default
	private Set<TimeUserModel> assignedUsers = new HashSet<>();

	@Column(precision = 10, scale = 2, nullable = false)
	@Builder.Default
	private BigDecimal hourlyRate = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(length = 3, nullable = false)
	@Builder.Default
	private CurrencyCode currency = CurrencyCode.EUR;

	@Column(nullable = false)
	@Builder.Default
	private boolean active = true;

	@Column(nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now(Clock.systemDefaultZone());
} 
