package pl.kurs.person.model.views;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@DiscriminatorValue("Retiree")
public class RetireeView extends PersonView{

    private Integer pension;
    private Integer yearsWorked;

    @Override
    protected void populateAdditionalFields() {
        getAdditionalFields().put("pension", this.pension);
        getAdditionalFields().put("yearsWorked", this.yearsWorked);
    }
}
