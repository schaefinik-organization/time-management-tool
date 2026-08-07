package schaefinik.time.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import schaefinik.time.enums.CurrencyCode;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponse {
	private Long id;
	private String name;
	private String description;
	private boolean active;

	private BigDecimal hourlyRate;
	private CurrencyCode currency;
}