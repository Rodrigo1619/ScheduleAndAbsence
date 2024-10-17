package com.masferrer.configs;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.masferrer.repository.UserRepository;
import com.masferrer.utils.UserInactiveException;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    //datos a usar para el manejo de emails
    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private String mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            //ver si el usuario tiene la porpiedad active en false
            if (!user.getActive()) {
                throw new UserInactiveException("User is inactive");
            }
    
            return user;
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        //seteando los datos para el manejo de emails
        javaMailSender.setHost(mailHost);
        javaMailSender.setPort(Integer.parseInt(mailPort));
        javaMailSender.setUsername(mailUsername);
        javaMailSender.setPassword(mailPassword);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");

        return javaMailSender;
    }
}
