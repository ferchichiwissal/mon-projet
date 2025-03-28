package pi.pperformance.elite.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doctor")
public class Doctor extends User {

    @OneToOne
    @JoinColumn(name = "cabinet_id", referencedColumnName = "idSite", unique = true)
    private CabinetDr cabinet;

    public Doctor() {
        super();
        this.setRole(Role.DOCTOR);
    }

    public Doctor(String firstName, String lastName, String email, String password, LocalDate birthDate, String tel, String address, CabinetDr cabinet) {
        super(firstName, lastName, email, password, birthDate, tel, address);
        this.setRole(Role.DOCTOR);
        this.cabinet = cabinet;
    }

    public CabinetDr getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetDr cabinet) {
        this.cabinet = cabinet;
    }
}
