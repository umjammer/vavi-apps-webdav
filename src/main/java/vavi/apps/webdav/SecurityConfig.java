/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.webdav;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import vavi.net.webdav.JavaFsWebDavServlet;


/**
 * SecurityConfig.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/08 umjammer initial version <br>
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String WEBDAV_ADMIN_PASSWORD = "WEBDAV_ADMIN_PASSWORD";
    public static final String WEBDAV_USER_PASSWORD = "WEBDAV_USER_PASSWORD";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests().antMatchers("/webdav/**").hasRole("USER")
                                .antMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().permitAll()
            .and()
            .httpBasic().realmName(JavaFsWebDavServlet.REALM)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("{noop}" + System.getenv(WEBDAV_USER_PASSWORD))
            .roles("USER");
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password("{noop}" + System.getenv(WEBDAV_ADMIN_PASSWORD))
            .roles("ADMIN", "USER");
    }
}
