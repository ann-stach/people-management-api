package pl.kurs.position.model.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CreatePositionCommand {

    private String name;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer salary;

}

