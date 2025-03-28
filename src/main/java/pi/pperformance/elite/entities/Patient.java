package pi.pperformance.elite.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Patients")
public class Patient extends User {

    @ManyToMany(mappedBy = "patients")
    private List<CabinetDr> cabinets = new ArrayList<>();
     
    public Patient() {
        super();
        this.setRole(Role.PATIENT);
    }

    public Patient(String firstName, String lastName, String email, String password, 
                  LocalDate birthDate, String tel, String address, CabinetDr cabinet) {
        super(firstName, lastName, email, password, birthDate, tel, address);
        this.setRole(Role.PATIENT);
        this.cabinets.add(cabinet);
    }

    public List<CabinetDr> getCabinets() {
        return cabinets;
    }

    public void setCabinets(List<CabinetDr> cabinets) {
        this.cabinets = cabinets;
    }
}
