package com.masferrer.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    //la llave secreta
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    //tiempo en milisegundos
    @Value("${jwt.exptime}")
    private Integer exptime;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject); //extraemos el sujeto del token (username o email)

    }
    //metodo para generar el token
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername()) //username es el email
            .setIssuedAt(new Date(System.currentTimeMillis())) //cuando el claim fue creado
            .setExpiration(new Date(System.currentTimeMillis() + exptime)) 
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();

    }
    //extraer un solo claim
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver){
        //primero extraer todos los claims
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); //retorna todos los claims de la lista que tenemos
    }
    //generar un token desde el userDetails
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    //validar un token
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    //extraer los claims
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
    }
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    
        String userIdString = (String) claims.get("sub");
        return UUID.fromString(userIdString);
    }

}
