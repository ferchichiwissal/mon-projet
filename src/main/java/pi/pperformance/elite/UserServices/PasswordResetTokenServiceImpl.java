/*package pi.pperformance.elite.UserServices;

 
import jakarta.mail.MessagingException;
import pi.pperformance.elite.UserRepository.PasswordResetTokenRepository;
import pi.pperformance.elite.UserRepository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository forgotPasswordRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
 
    public static final String INVALID_EMAIL_MESSAGE = "Please provide a valid email";

    //send mail for email verification
    public ResponseEntity<Response> verifyEmail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_EMAIL_MESSAGE));

        Optional<ForgotPasswordToken> fp =forgotPasswordRepository.findByUser(user);

        fp.ifPresent(forgotPasswordToken -> forgotPasswordRepository.deleteById(forgotPasswordToken.getId()));

        //time to formulate the mail body
        String generatedToken = tokenService.generateActivationCode();
        var token = ForgotPasswordToken.builder()
                .token(generatedToken)
                .expirationTime(new Date(System.currentTimeMillis() + 24 * 60 * 1000))
                .user(user)
                .build();

        forgotPasswordRepository.save(token);

        //Send Mail
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.RESET_PASSWORD,
                generatedToken,
                "OTP for Forgot Password request"
        );

        return ResponseEntity.ok(Response.builder()
                .message("\"Password reset OTP sent! You'll receive an email if you are registered on our system.\"")
                .build());

    }
    @Transactional
    public ResponseEntity<Response> verifyOtp(String token, String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_EMAIL_MESSAGE));

        ForgotPasswordToken fp =forgotPasswordRepository.findByTokenAndUser(token,user)
                .orElseThrow(()-> new RuntimeException("Invalid OTP for email "+email ));

        //Check if the expiration time of OTP is not expired
        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getId());
            verifyEmail(email);
            throw new RuntimeException("Forget Password token has expired. A new token has been send to the same email address");
        }
        fp.setVerified(true);

        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok(Response.builder()
                .message("OTP verified ")
                .build());

    }


    //Now User Can change the password

    public ResponseEntity<Response> changePasswordHandler(
            ChangePasswordResetRequest changePassword,
            String email
    ){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_EMAIL_MESSAGE));

        ForgotPasswordToken fp =forgotPasswordRepository.findByUser(user)
                .orElseThrow(()-> new RuntimeException("Invalid OTP for email "+email ));
        boolean areEqual = (changePassword.newPassword()).equals(changePassword.confirmationPassword());
        if (!areEqual){
            return new ResponseEntity<>(Response.builder()
                    .message("Please enter the password again!")
                    .build(), HttpStatus.EXPECTATION_FAILED);
        }
        if (fp.isVerified()){
            //We need to encode password
            String encodedPassword = passwordEncoder.encode(changePassword.newPassword());
            userRepository.updatePassword(email,encodedPassword);

            forgotPasswordRepository.deleteById(fp.getId());
            return ResponseEntity.ok(Response.builder()
                    .message("Password has been succesfully changed!")
                    .build());

        }

        return ResponseEntity.ok(Response.builder()
                .message("Otp User Not Verified!")
                .build());

    }



}*/