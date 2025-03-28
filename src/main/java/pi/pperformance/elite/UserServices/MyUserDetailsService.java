package pi.pperformance.elite.UserServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import pi.pperformance.elite.UserRepository.UserRepository;
import pi.pperformance.elite.entities.Role;
import pi.pperformance.elite.entities.User;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Check if the user is active
        if (!user.getIsActive()) {
            throw new DisabledException("User account is not activated. Please contact the administrator.");
        }

        // Get the role from the user and map it to a SimpleGrantedAuthority
        Role userRole = user.getRole();
        if (userRole == null) {
            throw new UsernameNotFoundException("User does not have a valid role.");
        }
        
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole.name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
    }
    
    
    
    public List<User> getUsersByIsActive(boolean is_active) {
        return userRepository.findByIsActive(is_active);
    }

    
    
    
    public List<User> getactiveUsers(boolean is_active) {
        return userRepository.findByIsActive(is_active);
    }
    
    

    
    
}
