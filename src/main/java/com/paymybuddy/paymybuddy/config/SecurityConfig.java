package com.paymybuddy.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for Spring Security.
 * Configures authentication, authorization, login, and logout behavior.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines a BCryptPasswordEncoder bean to be used for password hashing.
     * BCrypt is a strong hashing algorithm recommended for storing passwords securely.
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * - Allows public access to login, register, and static resources.
     * - Requires authentication for all other requests.
     * - Configures custom login and logout behavior.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to login, register, and static resources
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Configure form-based login
                .formLogin(form -> form
                        // Custom login page URL
                        .loginPage("/login")
                        // URL to submit login credentials
                        .loginProcessingUrl("/authenticate")
                        // Name of the username input field (email in this case)
                        .usernameParameter("email")
                        // Name of the password input field
                        .passwordParameter("password")
                        // Redirect URL after successful login
                        .defaultSuccessUrl("/dashboard", true)
                        // Redirect URL after failed login
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // Configure logout behavior
                .logout(logout -> logout
                        // URL to trigger logout
                        .logoutUrl("/logout")
                        // Redirect URL after successful logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        // Build and return the configured SecurityFilterChain
        return http.build();
    }
}
