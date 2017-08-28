package com.pigdroid.social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pigdroid.social.dto.ExampleUserDetails;
import com.pigdroid.social.repository.UserRepository;
import com.pigdroid.social.service.impl.RepositoryUserDetailsService;

/**
 * @author eduyayo@gmail.com
 */
@Configuration
@EnableWebSecurity
@Order(101)
public class RestSecurityContext extends WebSecurityConfigurerAdapter {

	private UserDetailsService userDetailsService = null;
	
	@Autowired
	private UserRepository userRepository;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
//        web
//	        .ignoring()
//	            .antMatchers("/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
                .antMatchers(
                		"/user/**",
                		"/users/**",
                		"/contacts**",
                		"/contacts/**",
                		"/contacts",
                		"/game/**",
                		"/games/**"
                		) // Rest services... ADD THE OTHERS HERE!
                .hasRole("USER")
                .and()
            	.httpBasic()
            /*.apply(new SpringSocialConfigurer()
        )*/.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Configures the authentication manager bean which processes authentication
     * requests.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AbstractUserDetailsAuthenticationProvider() {
			
			@Override
			protected UserDetails retrieveUser(String username,
					UsernamePasswordAuthenticationToken authentication)
					throws AuthenticationException {
				
				UserDetails details = userDetailsService().loadUserByUsername(username);
				return details;
			}
			
			@Override
			protected void additionalAuthenticationChecks(UserDetails userDetails,
					UsernamePasswordAuthenticationToken authentication)
					throws AuthenticationException {
				String credentials = (String) authentication.getCredentials();
				if(!BCrypt.checkpw(userDetails.getPassword(), credentials)) {
					throw new BadCredentialsException("Invalid credentials! \n\n Please, login again.");
				}
			}
		})
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
	 * This bean is load the user specific data when form login is used.
	 */
//	@Bean
//	public UserDetailsService userDetailsService() {
//		//TODO synch double check
//		if (userDetailsService == null) {
//			userDetailsService = new RepositoryUserDetailsService(){
//				
//				@Override
//				public UserDetails loadUserByUsername(String username)
//						throws UsernameNotFoundException {
//					ExampleUserDetails user = (ExampleUserDetails) super.loadUserByUsername(username);
//					
//			        ExampleUserDetails principal = ExampleUserDetails.getBuilder()
//			                .firstName(user.getFirstName())
//			                .id(user.getId())
//			                .lastName(user.getLastName())
//			                .password(userRepository.findByEmail(username).getUsr().getDeviceToken())
//			                .role(user.getRole())
//			                .socialSignInProvider(user.getSocialSignInProvider())
//			                .username(user.getUsername())
//			                .build();
//					return principal;
//				}
//			};
//		}
//		return userDetailsService;
//	}
    
    
//    /**
//     * This bean is used to load the user specific data when social sign in
//     * is used.
//     */
//    @Bean
//    public SocialUserDetailsService socialUserDetailsService() {
//        return new SimpleSocialUserDetailsService(userDetailsService());
//    }

//    /**
//     * This bean is load the user specific data when form login is used.
//     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new RepositoryUserDetailsService();
//    }
    
}
