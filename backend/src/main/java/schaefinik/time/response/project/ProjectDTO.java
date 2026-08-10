package schaefinik.time.response.project;

import lombok.Data;
import schaefinik.time.enums.CurrencyCode;
import schaefinik.time.response.user.UserSummaryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProjectDTO {
	private Long id;
	private String name;
	private String description;
	private BigDecimal hourlyRate;
	private BigDecimal internalHourlyRate;
	private CurrencyCode currency;
	private boolean active;
	private LocalDateTime creationDate;

	// Nur flache User-Referenzen!
	private UserSummaryDTO manager;
	private Set<UserSummaryDTO> assignedUsers;
}
