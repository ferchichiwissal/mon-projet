package pi.pperformance.elite.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Assistants")
public class Assistant extends User {

    @ManyToOne
    @JoinColumn(name = "cabinet_id", referencedColumnName = "idSite", nullable = false)
    private CabinetDr cabinet;

    public Assistant() {
        super();
        this.setRole(Role.ASSISTANT);
    }

    public Assistant(String firstName, String lastName, String email, String password, LocalDate birthDate, String tel, String address, CabinetDr cabinet) {
        super(firstName, lastName, email, password, birthDate, tel, address);
        this.setRole(Role.ASSISTANT);
        this.cabinet = cabinet;
    }

    public CabinetDr getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetDr cabinet) {
        this.cabinet = cabinet;
    }
}
