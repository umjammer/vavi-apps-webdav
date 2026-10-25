/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.webdav;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    public static final String WEBDAV_ADMIN_PASSWORD = "WEBDAV_ADMIN_PASSWORD";
    public static final String WEBDAV_USER_PASSWORD = "WEBDAV_USER_PASSWORD";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/webdav/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
        ).httpBasic(basic -> basic
                .realmName(JavaFsWebDavServlet.REALM)
        ).sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}" + System.getenv(WEBDAV_USER_PASSWORD))
                .authorities("USER").build();
        UserDetails admin = User.builder()
                .username("user")
                .password("{noop}" + System.getenv(WEBDAV_ADMIN_PASSWORD))
                .authorities("ADMIN", "USER").build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
