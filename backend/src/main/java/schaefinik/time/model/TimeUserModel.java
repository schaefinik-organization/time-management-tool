package schaefinik.time.model;

import jakarta.persistence.*;
import lombok.*;
import schaefinik.time.enums.Role;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "time_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeUserModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String username;

	@Column(nullable = false, unique = true, length = 190)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Role role;

	@Column(nullable = false)
	@Builder.Default
	private boolean enabled = true;

	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now(Clock.systemDefaultZone());

	@ManyToMany(mappedBy = "assignedUsers", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<ProjectModel> assignedProjects = new HashSet<>();
}
