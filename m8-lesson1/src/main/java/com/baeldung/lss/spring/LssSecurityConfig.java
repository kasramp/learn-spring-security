package com.baeldung.lss.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserRepository;

@EnableWebSecurity
@Configuration
public class LssSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public LssSecurityConfig(PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    //

    @PostConstruct
    private void saveTestUser() {
        final User user = new User();
        user.setEmail("test@email.com");
        user.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(user);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {// @formatter:off
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    } // @formatter:on

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// @formatter:off
        http
        .authorizeRequests()
                .antMatchers("/badUser*","/js/**").permitAll()
                .anyRequest().authenticated()

        .and()
        .formLogin().
            loginPage("/login").permitAll().
            loginProcessingUrl("/doLogin")

        .and()
        .logout().permitAll().logoutUrl("/logout")

        .and()
        .csrf().disable();
        return http.build();
    } // @formatter:on

}
