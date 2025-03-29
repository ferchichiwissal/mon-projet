package pi.pperformance.elite.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDate;

@Entity
@Table(name = "Patients")
public class Patient extends User {

    @ManyToOne
    @JoinColumn(name = "cabinet_id", nullable = false) // Each patient must belong to a cabinet
    private CabinetDr cabinet;

    public Patient() {
        super();
    }

    public Patient(String firstName, String lastName, String email, String password,
                  LocalDate birthDate, String tel, String address, CabinetDr cabinet) {
        super(firstName, lastName, email, password, birthDate, tel, address);
        this.setRole(Role.PATIENT);
        this.cabinet = cabinet; // Assign the single cabinet
    }

    public CabinetDr getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetDr cabinet) {
        this.cabinet = cabinet;
    }
}
