package pl.kurs.person.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.kurs.person.validation.annotation.CheckPesel;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@EqualsAndHashCode(of = "id")
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personIdGenerator")
    @SequenceGenerator(name = "personIdGenerator", sequenceName = "person_seq", initialValue = 1, allocationSize = 1)
    private int id;
    @Size(min = 3, max = 50)
    private String name;
    @Size(min = 3, max = 50)
    private String surname;
    @CheckPesel
    @Column(unique = true)
    private String pesel;
    @Min(1)
    @Max(250)
    private int height;
    @Min(1)
    @Max(350)
    private int weight;
    @Email
    private String email;
    @Column(name = "dtype", insertable = false, updatable = false)
    private String dtype;
    @Version
    private long version;


    public Person(String name, String surname, String pesel, int height, int weight, String email, long version) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.version = version;
    }

    public Person(Person person){
        this.id = person.getId();
        this.name = person.getName();
        this.surname = person.getSurname();
        this.pesel = person.getPesel();
        this.height = person.getHeight();
        this.weight = person.getWeight();
        this.email = person.getEmail();
        this.dtype = person.getDtype();
        this.version = person.getVersion();
    }

}
