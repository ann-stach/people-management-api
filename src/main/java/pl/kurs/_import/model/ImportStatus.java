package pl.kurs._import.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class ImportStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime submitDate;
    private LocalDateTime startDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private int processed;

    public ImportStatus() {
        this.submitDate = LocalDateTime.now();
        this.status = Status.NEW;
    }

    public enum Status {
        NEW, PROCESSING, SUCCESS, FAILED
    }
}
