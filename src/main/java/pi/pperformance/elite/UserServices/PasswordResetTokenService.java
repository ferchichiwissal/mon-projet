package pi.pperformance.elite.UserServices;

import org.springframework.http.ResponseEntity;

import dto.ChangePasswordResetRequest;
import dto.Response;
import jakarta.mail.MessagingException;

public interface PasswordResetTokenService {
 

 	    ResponseEntity<Response> verifyEmail(String email) throws MessagingException;

	    ResponseEntity<Response> verifyOtp(String otp, String email) throws MessagingException;

	    ResponseEntity<Response> changePasswordHandler(
	            ChangePasswordResetRequest changePasswordResetRequest,
	            String email
	    );

	}