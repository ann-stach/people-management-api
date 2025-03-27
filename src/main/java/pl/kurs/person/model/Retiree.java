package pl.kurs.person.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Retiree extends Person{

    @Positive
    private int pension;
    @Positive
    @Max(100)
    private int yearsWorked;

    public Retiree(String name, String surname, String pesel, int height, int weight, String email, long version, int pension, int yearsWorked) {
        super(name, surname, pesel, height, weight, email, version);
        this.pension = pension;
        this.yearsWorked = yearsWorked;
    }

    public Retiree(Retiree retiree) {
        super(retiree);
        this.pension = retiree.getPension();
        this.yearsWorked = retiree.getYearsWorked();
    }
}
