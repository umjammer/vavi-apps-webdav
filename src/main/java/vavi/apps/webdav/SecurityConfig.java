/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.webdav;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import vavi.net.webdav.JavaFsWebDavServlet;


/**
 * SecurityConfig.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/08 umjammer initial version <br>
 */
@Configuration
public class SecurityConfig {

    private static final Logger logger = System.getLogger(SecurityConfig.class.getName());

    public static final String WEBDAV_ADMIN_PASSWORD = "WEBDAV_ADMIN_PASSWORD";
    public static final String WEBDAV_USER_PASSWORD = "WEBDAV_USER_PASSWORD";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/webdav/**")
        ).authorizeHttpRequests(authz -> authz
                .requestMatchers("/webdav/**").hasRole("USER")
                .requestMatchers("/admin/**", "/login", "/redirect/**", "/actuator/**").hasRole("ADMIN")
                .anyRequest().permitAll()
        ).httpBasic(basic -> basic
                .realmName(JavaFsWebDavServlet.REALM)
        ).sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).build();
    }

    @Bean
    public UserDetailsService users() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String userPassword = System.getenv(WEBDAV_USER_PASSWORD);
        if (userPassword == null || userPassword.isBlank()) {
            userPassword = UUID.randomUUID().toString();
            logger.log(Level.WARNING, WEBDAV_USER_PASSWORD + " is not set; generated random password for 'user'");
        }
        String adminPassword = System.getenv(WEBDAV_ADMIN_PASSWORD);
        if (adminPassword == null || adminPassword.isBlank()) {
            adminPassword = UUID.randomUUID().toString();
            logger.log(Level.WARNING, WEBDAV_ADMIN_PASSWORD + " is not set; generated random password for 'admin'");
        }
        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode(userPassword))
                .roles("USER").build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode(adminPassword))
                .roles("ADMIN", "USER").build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
