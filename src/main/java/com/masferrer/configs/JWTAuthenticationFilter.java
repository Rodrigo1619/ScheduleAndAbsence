package com.masferrer.configs;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter{

    //para poder manejar el jwt
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader  = request.getHeader("Authorization");
        final String jwt;
        final String userEmail; //esto es el username para el UserDetail en la entidad

        //no debe ser null y el header debe iniciar con Bearer
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response); //para el siguiente filtro
            return;
        }
        //extraer el token del header
        jwt = authHeader.substring(7);  //debe empezar de la posicion 7 porque el token empezara despues de la palabra Bearer(6)
        
        //extraer user email del JWT
        userEmail = jwtService.extractUsername(jwt); //se deja username solamente porque spring security trabaja asi
        //ver si el email no es null y si el usuario no esta autenticado|conectado aun
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            //ver si el token todavia es valido o no
            if(jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
