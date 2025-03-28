package pi.pperformance.elite.UserController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pi.pperformance.elite.Authentif.JwtUtils;
import pi.pperformance.elite.UserServices.EmailService;
import pi.pperformance.elite.UserServices.UserServiceImplmnt;
import pi.pperformance.elite.entities.Role;
import pi.pperformance.elite.entities.User;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    UserServiceImplmnt userServices;


    @Autowired
    private EmailService emailService;
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
    String email = loginRequest.get("email");
    String password = loginRequest.get("password");
    log.info("Authenticating user with email: {}", email);
    
    try {
        // Authenticate user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        
        // Load user details (which includes authorities/roles)
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        // Get authorities (roles) from UserDetails
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        
        // Fetch user entity from DB
        User user = userServices.findByEmail(email);
        
        if (user == null || !user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "Account is inactive!",
                "errorCode", "AUTH002"
            ));
        }
        
        // Generate JWT tokens
        final String accessToken = jwtUtil.generateToken(userDetails.getUsername(), authorities); // Use authorities
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), authorities); // Use authorities
        
        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken,
            "isActive", user.getIsActive(),
            "roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()), // Add roles to the response
             "user", user // Send user data as part of the response

        ));
    } catch (AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}

    
//maram
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request){
        
        //extracting the refresh token from the request
        String refreshToken = request.get("refreshToken");
        if (refreshToken ==null){
            //checking if any problem happenned
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of( "message", "Refresh token is missing!",
            "errorCode", "AUTH003"));
        }

        //extracting email from the request
        String email;
        try{
            email = jwtUtil.extractEmail(refreshToken); // extract the email from the refresh token
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()
                //Map.of(
                //"message", "Invalid refresh token format!",
                //"errorCode", "AUTH004"
            //)
            );
        }
        // Get the roles (authorities) from the user details
Collection<? extends GrantedAuthority> authorities = userDetailsService.loadUserByUsername(email).getAuthorities();
// Convert authorities to a list of Role objects
        // Validate the refresh token using both email and token
    if (!jwtUtil.validateToken(refreshToken, email)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "message", "Invalid or expired refresh token!",
            "errorCode", "AUTH005"
        ));
    }
        String newAccessToken = jwtUtil.generateToken(email, authorities);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    
    
    
    
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        User user = userServices.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Email not found."));
        }

        String token = UUID.randomUUID().toString();
        userServices.savePasswordResetToken(user, token);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendEmail(email, "Password Reset Request", "Click the link to reset your password: " + resetLink);

        return ResponseEntity.ok(Map.of("message", "Password reset link sent."));
    }

   /* @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");

            if (token == null || newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Token and password are required"));
            }

            // Vérifier si le token est valide
            boolean isValid = userServices.validateResetToken(token);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Invalid or expired token"));
            }

            // Réinitialiser le mot de passe
            boolean isReset = userServices.resetPassword(token, newPassword);
            if (!isReset) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Password reset failed"));
            }

            return ResponseEntity.ok(Map.of("message", "Password successfully reset"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred: " + e.getMessage()));
        }
    }*/

}
