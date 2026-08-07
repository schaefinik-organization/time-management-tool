package schaefinik.time.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import schaefinik.time.enums.CurrencyCode;

import java.math.BigDecimal;

@Getter
@Setter
public class ProjectRequest {
	private String name;
	private String description;

	@NotNull(message = "Der Stundensatz darf nicht null sein.")
	@PositiveOrZero(message = "Der Stundensatz darf nicht negativ sein.")
	private BigDecimal hourlyRate;
	
	@NotNull(message = "Die Währung muss angegeben werden.")
	private CurrencyCode currency;

	public ProjectRequest() {
	}

	public ProjectRequest(String name, String description, BigDecimal hourlyRate, CurrencyCode currency) {
		this.name = name;
		this.description = description;
		this.hourlyRate = hourlyRate;
		this.currency = currency;
	}
}
