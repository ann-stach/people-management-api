package pl.kurs.person.model.views;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
public class PersonView {

    @Id
    private int id;
    private long version;
    @Column(insertable = false, updatable = false)
    private String dtype;
    private String name;
    private String surname;
    private String pesel;
    @Column(name = "gender", insertable = false, updatable = false)
    private String gender;
    private int height;
    private int weight;
    private String email;

    @Transient
    private Map<String, Object> additionalFields = new HashMap<>();

    public PersonView(long version, String dtype, String name, String surname, String pesel, String gender, int height, int weight, String email) {
        this.version = version;
        this.dtype = dtype;
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.email = email;

    }

    @PostLoad
    protected void loadAdditionalFields() {
        populateAdditionalFields();
    }

    protected void populateAdditionalFields(){

    };

    public <T> T getAdditionalField(String fieldName, Class<T> type) {
        return type.cast(additionalFields.get(fieldName));
    }
}
