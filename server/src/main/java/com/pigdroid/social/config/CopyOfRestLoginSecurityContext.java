package com.pigdroid.social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetailsService;

import com.pigdroid.social.repository.UserRepository;
import com.pigdroid.social.service.impl.RepositoryUserDetailsService;
import com.pigdroid.social.service.impl.SimpleSocialUserDetailsService;

/**
 * @author eduyayo@gmail.com
 */
@Configuration
@EnableWebSecurity
public class CopyOfRestLoginSecurityContext extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
	        .ignoring()
	            .antMatchers("/register/**"
	            		); // User registration
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
        	.httpBasic()
        	.and().authorizeRequests()
                .antMatchers("/login/**", "/profile/**").hasRole("USER")
                .and().authorizeRequests().anyRequest().permitAll()
               /* .and()
            .apply(new SpringSocialConfigurer() 
        ) */.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Configures the authentication manager bean which processes authentication
     * requests.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    /**
     * This is used to hash the password of the user.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * This bean is used to load the user specific data when social sign in
     * is used.
     */
    @Bean
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService(userDetailsService());
    }

    /**
     * This bean is load the user specific data when form login is used.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new RepositoryUserDetailsService();
    }
}
