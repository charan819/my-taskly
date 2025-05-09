package com.charan.mytaskly.config;

import com.charan.mytaskly.entities.Users;
import com.charan.mytaskly.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtTokenHelper {

    private final UsersRepository usersRepository;
    private final Environment environment;

    public JwtTokenHelper(UsersRepository usersRepository, Environment environment) {
        this.usersRepository = usersRepository;
        this.environment = environment;
    }

    public String generateToken(UserDetails userDetails){
        Optional<Users> existingUser = usersRepository.findByEmail(userDetails.getUsername());
        String secret = environment.getProperty("JWT_SECRET_KEY");
        String jwt_token = "";
        if (secret != null) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            jwt_token = Jwts
                    .builder()
                    .issuer("MyTaskly")
                    .subject("JwtToken")
                    .claim("user_id", existingUser.map(Users::getUserId).orElse(null))
                    .claim("name",existingUser.map(Users::getName).orElse(null))
                    .claim("email",userDetails.getUsername())
                    .claim("authorities",userDetails.getAuthorities()
                            .stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")))
                    .issuedAt(new Date())
                    .expiration(new Date(new Date().getTime()+30000000))
                    .signWith(secretKey).compact();
        }
        return jwt_token;
    }


    public Claims getClaimsFromToken(String token){
        String secret = environment.getProperty("JWT_SECRET_KEY");
        Claims claims = null;
        if (secret != null) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            claims = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        return claims;
    }

    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token){
        return getClaimsFromToken(token).getExpiration();
    }

    public String getUsernameFromToken(String token){
        return getClaimsFromToken(token).get("email").toString();
    }

    public Boolean validateToken(String token,UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
