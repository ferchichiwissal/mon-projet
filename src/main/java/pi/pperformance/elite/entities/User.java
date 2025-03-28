package pi.pperformance.elite.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)  // Utilisation de TABLE_PER_CLASS
@Table(name = "users")  // Classe mère avec ses attributs communs
public abstract class User implements Serializable, UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)  // Remplacez Identity par Auto
	private Long id;


    protected String firstName;
    protected String lastName;
    protected String email;
    protected LocalDate birthDate;
    protected String tel;
    protected String address;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    protected byte[] photoProfil;

    protected String gender;
    protected LocalDate createdAt;
    protected LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    protected Role role = Role.PATIENT;

    protected String password;
    protected boolean isActive;

    @Transient
    protected String confirmPassword;

    protected Integer age;

    public User() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.role = Role.PATIENT;
    }

    public User(String firstName, String lastName, String email, String password, LocalDate birthDate, String tel, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.tel = tel;
        this.address = address;
        this.isActive = false;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.role = Role.PATIENT;
        this.calculateAge();
    }

    @PrePersist
    @PreUpdate
    public void calculateAge() {
        if (birthDate != null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            this.age = 0;
        }
    }

    public void changeRole(Role newRole) {
        if (newRole != null) {
            this.role = newRole;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; calculateAge(); }
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public byte[] getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(byte[] photoProfil) { this.photoProfil = photoProfil; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
