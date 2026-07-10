package schaefinik.time.timeentry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateTimeEntryRequest(
                @NotNull(message = "Projekt-ID ist erforderlich") Long projectId,

                @NotNull(message = "Datum ist erforderlich") LocalDate entryDate,

                @NotNull(message = "Startzeit ist erforderlich") LocalTime startTime,

                @NotNull(message = "Endzeit ist erforderlich") LocalTime endTime,

                @Size(max = 500, message = "Notiz darf maximal 500 Zeichen lang sein") String note) {
}
