package pl.kurs.person.facades.personFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kurs.person.model.Retiree;
import pl.kurs.person.model.dto.RetireeDto;

import java.util.Map;

@Component("retireeFacade")

@RequiredArgsConstructor
public class RetireeFacade implements PersonFacade<Retiree, RetireeDto> {

    private static final String INSERT_RETIREE_SQL = "INSERT INTO person " +
            "(id, dtype, name, surname, pesel, height, weight, email, pension, years_worked, version) " +
            "VALUES (NEXT VALUE FOR person_seq, 'Retiree', ?, ?, ?, ?, ?, ?, ?, ?, 0)";


    @Override
    public Retiree createPersonInternal(Map<String, String> parameters) {
        Retiree retiree = new Retiree();
        retiree.setName(parameters.get("name"));
        retiree.setSurname(parameters.get("surname"));
        retiree.setPesel(parameters.get("pesel"));
        retiree.setHeight(Integer.parseInt(parameters.get("height")));
        retiree.setWeight(Integer.parseInt(parameters.get("weight")));
        retiree.setEmail(parameters.get("email"));
        retiree.setPension(Integer.parseInt(parameters.get("pension")));
        retiree.setYearsWorked(Integer.parseInt(parameters.get("yearsWorked")));
        return retiree;
    }

    @Override
    public RetireeDto toDto(Retiree retiree) {
        return new RetireeDto(
                retiree.getId(),
                retiree.getDtype(),
                retiree.getName(),
                retiree.getSurname(),
                retiree.getPesel(),
                retiree.getHeight(),
                retiree.getWeight(),
                retiree.getEmail(),
                retiree.getPension(),
                retiree.getYearsWorked());
    }

    @Override
    public Retiree editPersonInternal(Retiree retiree, Map<String, String> parameters, long version) {
        Retiree copy = clone(retiree);

        updateField(parameters, "name", copy::setName);
        updateField(parameters, "surname", copy::setSurname);
        updateField(parameters, "pesel",copy::setPesel);
        updateField(parameters, "height", height -> copy.setHeight(Integer.parseInt(height)));
        updateField(parameters, "weight", weight -> copy.setWeight(Integer.parseInt(weight)));
        updateField(parameters, "email", copy::setEmail);
        updateField(parameters, "pension", pension -> copy.setPension(Integer.parseInt(pension)));
        updateField(parameters, "yearsWorked", yearsWorked -> copy.setYearsWorked(Integer.parseInt(yearsWorked)));
        copy.setVersion(version);

        return copy;
    }

    @Override
    public Retiree clone(Retiree retiree) {
        return new Retiree(retiree);
    }

    @Override
    public String[] getSqlArgs(String[] args) {
        return new String[] {args[1], args[2], args[3], args[4], args[5], args[6], args[11], args[12]};
    }

    @Override
    public String getSql() {
        return INSERT_RETIREE_SQL;
    }
}
