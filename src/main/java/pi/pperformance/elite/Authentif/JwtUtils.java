package pi.pperformance.elite.Authentif;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import pi.pperformance.elite.entities.Role;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
/*Marks this class as a Spring component,
so it can be automatically detected and managed by the Spring container.
This allows it to be injected into other classes where needed.*/
public class JwtUtils {
    /* Generate a strong secret key for HS256
     * It’s a method to mix the secret key with the data inside the token to create a secure signature. This signature is then used to ensure the token hasn’t been changed by anyone else.*/
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Ensure a strong dynamic 256-bit key 

    
    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
     // Retrieve email from JWT token
     public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Retrieve expiration date from JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities) {
    List<String> roleNames = authorities.stream()
                                        .map(GrantedAuthority::getAuthority)  // Convert GrantedAuthority to string
                                        .collect(Collectors.toList());

    return Jwts.builder()
            .setSubject(email)
            .claim("roles", roleNames)  // Adding roles as a claim
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
            .signWith(SECRET_KEY)
            .compact();
}

    //Generate a refresh token for the user
    public String generateRefreshToken(String email, Collection<? extends GrantedAuthority> authorities) {
        List<String> roleNames = authorities.stream()
                                            .map(GrantedAuthority::getAuthority)  // Convert GrantedAuthority to string
                                            .collect(Collectors.toList());
    
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roleNames)  // Adding roles as a claim
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days validity
                .signWith(SECRET_KEY)
                .compact();
    }

    // Check if the token has expired
     private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    // Validate token
    public Boolean validateToken(String token, String email) {
        final String emailFromToken = extractEmail(token);
        return (emailFromToken.equals(email) && !isTokenExpired(token));
    }

    // Get roles from the token
    public List<Role> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        List<String> roleNames = claims.get("roles", List.class); // Extract roles as strings
        return roleNames.stream()
                        .map(Role::valueOf)  // Convert strings back to Role enum
                        .collect(Collectors.toList());
    }
}
