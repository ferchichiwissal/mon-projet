package pi.pperformance.elite.entities;

import java.sql.Date;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class CabinetDr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSite;
    
    private String address;
    private String fax;
    private String tel;
    private String name;
    private String taxNumber;
    private Date createdAt;
    private Date updatedAt;

    @OneToOne(mappedBy = "cabinet", cascade = CascadeType.ALL)
    private Doctor doctor;

    @OneToMany(mappedBy = "cabinet", cascade = CascadeType.ALL)
    private List<Assistant> assistants;

    @ManyToMany
    @JoinTable(
      name = "cabinet_patient", 
      joinColumns = @JoinColumn(name = "cabinet_id"), 
      inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    private List<Patient> patients;

    // Constructeurs
    public CabinetDr() {}

    public CabinetDr(Long idSite, String address, String fax, String tel, String name, String taxNumber, Date createdAt, Date updatedAt) {
        this.idSite = idSite;
        this.address = address;
        this.fax = fax;
        this.tel = tel;
        this.name = name;
        this.taxNumber = taxNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public Long getIdSite() { return idSite; }
    public void setIdSite(Long idSite) { this.idSite = idSite; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public List<Assistant> getAssistants() { return assistants; }
    public void setAssistants(List<Assistant> assistants) { this.assistants = assistants; }

    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }
}
