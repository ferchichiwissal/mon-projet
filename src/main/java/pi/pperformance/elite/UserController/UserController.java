package pi.pperformance.elite.UserController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pi.pperformance.elite.Authentif.JwtUtils;
import pi.pperformance.elite.UserServices.EmailService;
import pi.pperformance.elite.UserServices.RecaptchaService;
import pi.pperformance.elite.UserServices.UserServiceInterface;
import pi.pperformance.elite.UserServices.VerificationService;
import pi.pperformance.elite.entities.Admin;
import pi.pperformance.elite.entities.Assistant;
import pi.pperformance.elite.entities.Doctor;
import pi.pperformance.elite.entities.Patient;
import pi.pperformance.elite.entities.Role;
import pi.pperformance.elite.entities.User;
import pi.pperformance.elite.entities.VerificationRequest;
import pi.pperformance.elite.exceptions.AccountNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import pi.pperformance.elite.UserRepository.*;
 @RestController
@RequestMapping("/Users")
public class UserController {
	@Autowired
    private UserRepository UsrRepo;
	@Autowired
    private 	PatientRepository  PatientRepo;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserServiceInterface usrService; // Autowiring the service interface here to access the functions we declared there
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private RecaptchaService RecaptchaService;
    // Add function that allows us to add a active  user to the database
    @PostMapping("/add")
    public ResponseEntity<String> addUser(
            @RequestParam("first_name") String first_name,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("birthDate") String birthDate,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam("password") String password,
            @RequestParam("tel") String tel,
            @RequestParam("address") String address,
            @RequestParam("gendre") String gendre,
            @RequestParam(value = "photoProfil", required = false) MultipartFile photoProfil) {

        if (usrService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }

        if (photoProfil != null && !photoProfil.isEmpty()) {
            if (!photoProfil.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Invalid file type. Only images are allowed.");
            }
            if (photoProfil.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size exceeds the limit of 5 MB.");
            }
        }

        User user = new Patient(); // Crée un utilisateur avec le rôle PATIENT par défaut
        user.setFirstName(first_name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setBirthDate(LocalDate.parse(birthDate));
        user.setPassword(password);
        user.setTel(tel);
        user.setAddress(address);
        user.setGender(gendre);
        user.setRole(Role.PATIENT); // Forcer le rôle PATIENT par défaut
        user.setIsActive(true);

        if (photoProfil != null && !photoProfil.isEmpty()) {
            try {
                user.setPhotoProfil(photoProfil.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process profile image.");
            }
        }

        User savedUser = usrService.addUser(user, true);
        return ResponseEntity.ok("Registration successful! Your account is pending approval by an admin.");
    }

    @PostMapping("/addInactive")
    public ResponseEntity<String> addInactiveUser(
            @RequestParam("first_name") String first_name,
            @RequestParam("last_name") String lastName,
            @RequestParam("email") String email,
            @RequestParam("birthDate") String birthDate,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam("password") String password,
            @RequestParam("tel") String tel,
            @RequestParam("address") String address,
            @RequestParam("gendre") String gendre,
            @RequestParam(value = "photoProfil", required = false) MultipartFile photoProfil,
            @RequestParam("g-recaptcha-response") String captchaResponse) {

        // Vérifier le reCAPTCHA
        if (!RecaptchaService.verifyCaptcha(captchaResponse)) {
            return ResponseEntity.badRequest().body("reCAPTCHA verification failed.");
        }

        // Vérifier si l'email existe déjà
        if (usrService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }

        // Valider la photo de profil si elle est fournie
        if (photoProfil != null && !photoProfil.isEmpty()) {
            if (!photoProfil.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Invalid file type. Only images are allowed.");
            }
            if (photoProfil.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size exceeds the limit of 5 MB.");
            }
        }

        // Convertir la photo de profil en byte[]
        byte[] photoProfilBytes = null;
        if (photoProfil != null && !photoProfil.isEmpty()) {
            try {
                photoProfilBytes = photoProfil.getBytes();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process profile image.");
            }
        }

        // Générer un code de vérification
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        // Stocker l'utilisateur temporairement
        VerificationRequest verificationRequest = new VerificationRequest(
                first_name, lastName, email, birthDate, password, tel, address, gendre, role, photoProfilBytes, verificationCode
        );
        verificationService.storeVerificationRequest(email, verificationRequest);

        // Envoyer un email avec le code de vérification
        emailService.sendVerificationEmail(email, verificationCode);

        return ResponseEntity.ok("Verification code sent to email.");
    }
    
    
    
    
    
    

    @PostMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        VerificationRequest verificationRequest = verificationService.getVerificationRequest(email);

        if (verificationRequest == null || !verificationRequest.getVerificationCode().equals(code)) {
            return ResponseEntity.badRequest().body("Invalid or expired verification code.");
        }

        // Persist user in database
        User user = new Patient();
        user.setFirstName(verificationRequest.getFirstName());
        user.setLastName(verificationRequest.getLastName());
        user.setEmail(verificationRequest.getEmail());
        user.setBirthDate(LocalDate.parse(verificationRequest.getBirthDate()));
        user.setPassword(verificationRequest.getPassword());
        user.setTel(verificationRequest.getTel());
        user.setAddress(verificationRequest.getAddress());
        user.setGender(verificationRequest.getGendre());
        user.setRole(verificationRequest.getRole() != null ? Role.valueOf(verificationRequest.getRole()) : Role.PATIENT);
        user.setIsActive(false);

        // Définir la photo de profil si elle est fournie
        if (verificationRequest.getPhotoProfil() != null) {
            user.setPhotoProfil(verificationRequest.getPhotoProfil());
        }

        usrService.addUser(user,true);

        // Supprimer de la base de données temporaire
        verificationService.removeVerificationRequest(email);

        return ResponseEntity.ok("Account successfully verified and activated!");
    }
    

    @PostMapping("/transferUser")
    public ResponseEntity<String> transferUser(
            @RequestParam Long userId,
            @RequestParam String targetRole, boolean passwordToBeEncrypted) {

        // Trouver l'utilisateur à transférer (Patient dans ce cas)
        User user = usrService.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        // Vérifier si le rôle cible est valide
        Role newRole;
        try {
            newRole = Role.valueOf(targetRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid target role.");
        }

        // Assurez-vous que l'utilisateur est un Patient avant de le transférer
        if (user.getRole() != Role.PATIENT) {
            return ResponseEntity.badRequest().body("User is not a Patient.");
        }

        // Créer un nouvel utilisateur avec le rôle cible (Doctor/Admin)
        User newUser;
        if (newRole == Role.DOCTOR) {
            newUser = new Doctor();
        } else if (newRole == Role.ADMIN) {
            newUser = new Admin();
        } 
        
        else if (newRole == Role.ASSISTANT) {
            newUser = new Assistant();
        }
        else {
            return ResponseEntity.badRequest().body("Unsupported role transfer.");
        }

        // Copier les informations du Patient vers le nouveau rôle
     // Copier les informations du Patient vers le nouveau rôle
        copyUserDetails(user, newUser, newRole);

        // Sauvegarder le nouvel utilisateur dans la table appropriée (Doctor ou Admin)
        usrService.addUser(newUser, false);

        // Facultatif : Supprimer le Patient de la table Patient
        if (user instanceof Patient) {
            usrService.deletePatient((Patient) user);  // Suppression de l'utilisateur de la table Patient
        }

        return ResponseEntity.ok("User successfully transferred to " + newRole.name());
    }

    // Refactor: Utility method to copy user details
    private void copyUserDetails(User from, User to, Role targetRole) {
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhotoProfil(from.getPhotoProfil()); // Copier la photo de profil

        to.setBirthDate(from.getBirthDate());
        to.setPassword(from.getPassword());
        to.setTel(from.getTel());
        to.setAddress(from.getAddress());
        to.setGender(from.getGender());
        to.setRole(targetRole); // Appliquer le nouveau rôle au lieu de conserver l'ancien
        to.setIsActive(true);  // Assurez-vous que l'utilisateur transféré soit actif
    }

    // Ajout d'une méthode pour supprimer un Patient
    public void deletePatient(Patient patient) {
        if (patient != null) {
            PatientRepo.delete(patient);
        } else {
            throw new IllegalArgumentException("Cannot delete null Patient");
        }
    }

    public User getUserById1(Long id) {
        return UsrRepo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("User with ID " + id + " not found"));
    }
    
    
   
     


    
    
    
    
    
    
  
    
    
    
     

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 
    
    
    
    // Get all users
    @GetMapping("/alluser")
    public List<User> getAllUsers() {
        return usrService.getAllUsers();
    }

    // Get user by email with proper exception handling
    @GetMapping("/useremail/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            User user = usrService.getUserByEmail(email);
            return ResponseEntity.ok(user); // Return user if found
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // Return 404 if not found
        }
    }

    // Get user by ID
    @GetMapping("/allid/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = usrService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user); // Return user if found
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found");
        }
    }

    // Update user details
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = usrService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);}
  
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            usrService.deleteUser(id);
            return ResponseEntity.ok("User with ID " + id + " has been deleted successfully.");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found.");
        }
    }
    
    
   // Find user by email
    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        User user = usrService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
  //  handle status 
     
    /*@PutMapping("/status/{id}")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        User updatedUser = usrService.toggleUserStatus(id);

        String newStatus = updatedUser.getIsActive() ? "active" : "inactive";
        return ResponseEntity.ok("User with ID " + id + " is now " + newStatus);
    }*/

//wissal 

    // Get all inactive users (role = 2)
    @GetMapping("/inactiveusers")
    public List<User> getInactiveUsers() {
        return usrService.getUsersByIsActive(false);
    }
      
    
    @GetMapping("/patients")
    public ResponseEntity<List<User>> getPatients() {
        List<User> patients = usrService.getPatients();
        return ResponseEntity.ok(patients);
    }
    
    
    
    
    
    @GetMapping("/activeusers")
    public List<User> getactiveUsers() {
        return usrService.getactiveUsers(true);
    }

    
    @PutMapping("/changeractivateUser/{id}")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        try {
            User updatedUser = usrService.activateUser(id);
            return ResponseEntity.ok(updatedUser);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PutMapping("/changerDesactivateUser/{id}")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        try {
            User updatedUser = usrService.deactivateUser(id);  // Méthode pour désactiver l'utilisateur
            return ResponseEntity.ok(updatedUser);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
//wissal
    }
    }