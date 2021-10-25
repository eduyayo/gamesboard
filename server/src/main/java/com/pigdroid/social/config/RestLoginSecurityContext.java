//package com.pigdroid.social.config;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//import javax.servlet.Filter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCrypt;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.social.security.SocialUserDetailsService;
//
//import com.pigdroid.social.model.user.User;
//import com.pigdroid.social.repository.UserRepository;
//import com.pigdroid.social.service.impl.RepositoryUserDetailsService;
//import com.pigdroid.social.service.impl.SimpleSocialUserDetailsService;
//
///**
// * @author eduyayo@gmail.com
// */
//@Configuration
//@EnableWebSecurity
//public class RestLoginSecurityContext extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private UserRepository userRepository;
//	private UserDetailsService userDetailsTokenService;
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//	        .ignoring()
//	            .antMatchers("/register/**"
//	            		); // User registration
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//		http.addFilterBefore(tokenProcessingFilter(), BasicAuthenticationFilter.class).csrf().disable().httpBasic()
//        	.and().authorizeRequests()
//                .antMatchers("/login/**", "/profile/**").hasRole("USER")
//                .and().authorizeRequests().anyRequest().permitAll()
//               /* .and()
//            .apply(new SpringSocialConfigurer()
//        ) */
//                .and().authorizeRequests().antMatchers(
//                		"/user/**",
//                		"/users/**",
//                		"/contacts**",
//                		"/contacts/**",
//                		"/contacts",
//                		"/game/**",
//                		"/games/**"
//                		).hasRole("USER")
//                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                ;
//    }
//
//    private Filter tokenProcessingFilter() {
//		return new BasicAuthenticationFilter(new AuthenticationManager() {
//
//			@Override
//			public Authentication authenticate(Authentication authentication)
//					throws AuthenticationException {
//				User user = userRepository.findByEmail(authentication.getName());
//				if (user == null || user.getUsr() == null) {
//					throw new BadCredentialsException("Invalid credentials! \n\n Please, login again.");
//				}
//				String token = user.getUsr().getDeviceToken();
//				String credentials = (String) authentication.getCredentials();
//				try {
//					if(!BCrypt.checkpw(token, credentials)) {
//						throw new BadCredentialsException("Invalid credentials! \n\n Please, login again.");
//					}
//				} catch (IllegalArgumentException e) {
//					throw new BadCredentialsException("Invalid credentials! \n\n Please, login again.");
//				}
//				Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//				authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
//				Authentication ret = new PreAuthenticatedAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities );
//				return ret;
//			}
//		}) {
//			@Override
//			protected boolean isIgnoreFailure() {
//				return true;
//			}
//		};
//	}
//
//	/**
//     * Configures the authentication manager bean which processes authentication
//     * requests.
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(userDetailsService())
//                .passwordEncoder(passwordEncoder());
//    }
//
//    /**
//     * This is used to hash the password of the user.
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(10);
//    }
//
//    /**
//     * This bean is used to load the user specific data when social sign in
//     * is used.
//     */
//    @Bean
//    public SocialUserDetailsService socialUserDetailsService() {
//        return new SimpleSocialUserDetailsService(userDetailsService());
//    }
//
//    /**
//     * This bean is load the user specific data when form login is used.
//     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new RepositoryUserDetailsService();
//    }
//
////	@Bean
////	public UserDetailsService userDetailsTokenService() {
////		//TODO synch double check
////		if (userDetailsTokenService == null) {
////			userDetailsTokenService = new RepositoryUserDetailsService(){
////
////				@Override
////				public UserDetails loadUserByUsername(String username)
////						throws UsernameNotFoundException {
////					ExampleUserDetails user = (ExampleUserDetails) super.loadUserByUsername(username);
////
////			        ExampleUserDetails principal = ExampleUserDetails.getBuilder()
////			                .firstName(user.getFirstName())
////			                .id(user.getId())
////			                .lastName(user.getLastName())
////			                .password(userRepository.findByEmail(username).getUsr().getDeviceToken())
////			                .role(user.getRole())
////			                .socialSignInProvider(user.getSocialSignInProvider())
////			                .username(user.getUsername())
////			                .build();
////					return principal;
////				}
////			};
////		}
////		return userDetailsTokenService;
////	}
//}
