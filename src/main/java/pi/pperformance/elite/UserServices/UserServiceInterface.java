package pi.pperformance.elite.UserServices;
import java.util.List;

import pi.pperformance.elite.entities.Patient;
import pi.pperformance.elite.entities.Role;
import pi.pperformance.elite.entities.User;


//the service interface where we're going to declare the function we'll use in both the controller and the service class
public interface UserServiceInterface{
//please make sure to name the entity "User" so the code recognize it and most of the red underlined User will be gone
  
    public User updateUser(Long id, User userDetails); 
    public void deleteUser(Long id);
    public List<User> getAllUsers();
    public User getUserByEmail(String email);
    public User getUserById(Long id);
    User findByEmail(String email);
     // marwa
    //User toggleUserStatus(Long id);
    
  
    
    
    
    
      
    public List<User> getUsersByIsActive(boolean is_active);
    public List<User> getactiveUsers(boolean is_notactive) ;
    public   User activateUser(Long id);
    public User deactivateUser(Long id);
    //wissal

    User changeUserRole(Long userId, Role newRole); // Nouvelle méthode

	public User findById(Long userId);

	public void deletePatient(Patient user);
    public User addUser(User user,boolean passwordToBeEncrypted);
 	public List<User> getPatients();
	boolean validateResetToken(String token);
	//boolean resetPassword(String token, String newPassword);
 
 
  }
