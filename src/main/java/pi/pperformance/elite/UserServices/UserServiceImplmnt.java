package pi.pperformance.elite.UserServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
  import pi.pperformance.elite.UserRepository.PasswordResetTokenRepository;
 
import pi.pperformance.elite.UserRepository.UserRepository;
import pi.pperformance.elite.entities.Admin;
import pi.pperformance.elite.entities.Assistant;
import pi.pperformance.elite.entities.Doctor;
import pi.pperformance.elite.entities.PasswordResetToken;
import pi.pperformance.elite.entities.Patient;
import pi.pperformance.elite.entities.Role;
import pi.pperformance.elite.entities.User;
import pi.pperformance.elite.exceptions.AccountNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplmnt implements UserServiceInterface {

    @Autowired
    private UserRepository UsrRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

 
 
    @Autowired
    private EmailService emailService;
 
    private final Map<String, String> tokenStorage = new HashMap<>(); // Temporary storage for tokens (use a database in production)

    

    @Override
    public boolean validateResetToken(String token) {
        return tokenStorage.containsKey(token);
    }

   /* @Override
    public boolean resetPassword(String token, String newPassword) {
        // Vérifier si le token existe dans la base de données
        PasswordResetToken resetToken = PasswordResetTokenRepository .findByToken(token);
        if (resetToken == null) {
            return false; // Token invalide
        }

        // Vérifier si le token est expiré
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken); // Supprimer le token expiré
            return false;
        }

        // Récupérer l'utilisateur associé au token
        User user = resetToken.getUser();
        if (user == null) {
            return false;
        }

        // Hasher le nouveau mot de passe avant de l’enregistrer
        user.setPassword(passwordEncoder.encode(newPassword));

        // Sauvegarder le nouvel utilisateur avec le mot de passe mis à jour
        userRepository.save(user);

        // Supprimer le token après la réinitialisation du mot de passe
        tokenRepository.delete(resetToken);

        return true;
    }
    */
    
    
    // Add a new user and hash the password before saving
    @Override
    public User addUser(User user,boolean passwordToBeEncrypted) {
    	  // Hash the password before saving
        String encodedPassword = passwordToBeEncrypted ? passwordEncoder.encode(user.getPassword())   : user.getPassword();
        user.setPassword(encodedPassword);  // Set the hashed password
      

        return UsrRepo.save(user);}
    // Returns a list of all users
    @Override
    public List<User> getAllUsers() {
        return UsrRepo.findAll();
    }

    // Returns a user by id, throws AccountNotFoundException if not found
   
    // Returns a user by email. Throws AccountNotFoundException if not found.
    @Override
    public User getUserByEmail(String email) {
        User user = UsrRepo.findByEmail(email);
        if (user == null) {
            throw new AccountNotFoundException("User with email " + email + " not found");
        }
        return user;
    }

    // Updates a user's information if they exist; throws AccountNotFoundException if not found
    @Override
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = UsrRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setAddress(userDetails.getAddress());
            user.setTel(userDetails.getTel());

            user.setBirthDate(userDetails.getBirthDate());
            user.setRole(userDetails.getRole());
            // If password is being updated, encode it
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(userDetails.getPassword());
                user.setPassword(encodedPassword);
            }

            return UsrRepo.save(user);
        } else {
            throw new AccountNotFoundException("User with ID " + id + " not found");
        }
    }

    // Deletes a user by id; throws AccountNotFoundException if user doesn't exist
    @Override
    public void deleteUser(Long id) {
        if (UsrRepo.existsById(id)) {
            UsrRepo.deleteById(id);
        } else {
            throw new AccountNotFoundException("User with ID " + id + " does not exist");
        }
    }

    
    
    
    
    // Finds a user by email, returns null if not found
    @Override
    public User findByEmail(String email) {
        return UsrRepo.findByEmail(email);
    }
    
   
    
      // Marwa
    
    /* 
     @Override
    public User toggleUserStatus(Long id) {
        // Retrieve the user by ID
        User user = UsrRepo.findById(id)
         .orElseThrow(() -> new AccountNotFoundException("User with ID " + id + " not found"));
        
        
        // Toggle the current isActive status
        user.setIsActive(!user.getIsActive());
        
        // Save and return the updated user
        return UsrRepo.save(user);
    }
       */
    
      //Marwa


    
    //wissal 
    
    
    
    @Override
	public List<User> getactiveUsers (boolean is_active) {
	 
		 return UsrRepo.findByIsActive(is_active);
	}
    
    
    @Override
    public List<User> getPatients() {
        return UsrRepo.findByRole(Role.PATIENT);
    }
    
    
    @Override
	public List<User> getUsersByIsActive(boolean is_active) {
		 
		 return UsrRepo.findByIsActive(is_active);
	}
    
    
    
    
    
    
    
   
    @Override
	public User activateUser(Long id) {
	    // Retrieve the user from the database by ID
	    User user = UsrRepo.findById(id)
	            .orElseThrow(() -> new AccountNotFoundException("User with ID " + id + " not found"));

	    // If the user is not active, set the status to active (true)
	    if (!user.getIsActive()) {
	        user.setIsActive(true); // Automatically activate the user without requiring further input
	        UsrRepo.save(user); // Save the updated user object to the database
	    }

	    // Return the updated user object
	    return user;
	}

	 
	public User deactivateUser(Long id) throws AccountNotFoundException {
	    User user = UsrRepo.findById(id).orElseThrow(() -> new AccountNotFoundException("Utilisateur non trouvé"));
	    user.setIsActive(false); // Désactive l'utilisateur
	    return UsrRepo.save(user);
	}


   /* @Override
	public User changeUserRole(Long id, Role newRole) {
	    User user = UsrRepo.findById(id).orElseThrow(() -> new AccountNotFoundException("User not found"));
	    user.setRole(newRole);
	    return UsrRepo.save(user);
	}
*/

    
    
    public void savePasswordResetToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);
    }





    @Transactional
    public User changeUserRole(Long userId, Role newRole) {
        // Récupérer l'utilisateur existant
        User existingUser = UsrRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si le nouveau rôle est valide
        if (newRole != Role.DOCTOR && newRole != Role.ASSISTANT && newRole != Role.ADMIN) {
            throw new RuntimeException("Invalid role");
        }

        // Créer une nouvelle instance en fonction du nouveau rôle
        User newUser;
        switch (newRole) {
            case DOCTOR:
                newUser = new Doctor();
                break;
            case ASSISTANT:
                newUser = new Assistant();
                break;
            case ADMIN:
                newUser = new Admin();
                break;
            default:
                throw new RuntimeException("Invalid role");
        }

        // Copier les attributs de l'utilisateur existant vers la nouvelle instance
        newUser.setId(existingUser.getId());
        newUser.setFirstName(existingUser.getFirstName());
        newUser.setLastName(existingUser.getLastName());
        newUser.setEmail(existingUser.getEmail());
        newUser.setPassword(existingUser.getPassword());
        newUser.setBirthDate(existingUser.getBirthDate());
        newUser.setTel(existingUser.getTel());
        newUser.setAddress(existingUser.getAddress());
        newUser.setGender(existingUser.getGender());
        newUser.setPhotoProfil(existingUser.getPhotoProfil());
        newUser.setCreatedAt(existingUser.getCreatedAt());
        newUser.setUpdatedAt(LocalDate.now());
        newUser.setRole(newRole);

        // Supprimer l'ancien utilisateur
        UsrRepo.delete(existingUser);

        // Enregistrer le nouvel utilisateur
        return userRepository.save(newUser);
    }
	@Override
	public User findById(Long userId) {
        return UsrRepo.findById(userId).orElse(null);
 	}
	
	


    

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    // Méthode pour supprimer un Patient de la table Patient
    public void deletePatient(Patient patient) {
        userRepository.delete(patient);
    }
	@Override
	public User getUserById(Long id) {
        return UsrRepo.findById(id).orElse(null);
		
	}

 
	 
	 
    






















}
























 
    






    
    //wissal

