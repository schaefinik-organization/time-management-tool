package schaefinik.time.request.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import schaefinik.time.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProjectCreateRequest {
	private String name;
	private String description;

	@NotNull(message = "Der Stundensatz darf nicht NULL sein.")
	@PositiveOrZero(message = "Der Stundensatz darf nicht negativ sein.")
	private BigDecimal hourlyRate;

	@NotNull(message = "Der Stundensatz darf nicht NULL sein.")
	@PositiveOrZero(message = "Der Stundensatz darf nicht negativ sein.")
	private BigDecimal internalHourlyRate;

	@NotNull(message = "Die Währung muss angegeben werden.")
	private CurrencyCode currency;

	private Set<Long> assignedUserIds;
}
