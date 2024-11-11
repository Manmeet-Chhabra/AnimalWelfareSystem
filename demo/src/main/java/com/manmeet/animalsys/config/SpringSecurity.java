package com.manmeet.animalsys.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize

				.requestMatchers("/register/**", "/login", "/index", "/donations", "/donations/**", "/images/**",
						"/css/**", "/js/**", "/about", "/education", "/pet-care-tips", "/forum", "/forum/pet-care",
						"/forum/adoption", "/forum/volunteering", "/submit-feedback", "/feedback", "/thank-you", "/recent-donors")
				.permitAll().requestMatchers("/dashboard", "/donations/recent-donors").authenticated() // All users can access the dashboard
				.requestMatchers("/users").hasRole("ADMIN") // Admin only access for users
				.anyRequest().authenticated() // All other requests require authentication
		).formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").successHandler(customSuccessHandler()) // Custom
																															// success
																															// handler
				.permitAll()).csrf().disable()
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll());
		return http.build();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	// Custom success handler for role-based redirection
	@Bean
	public AuthenticationSuccessHandler customSuccessHandler() {
		return (request, response, authentication) -> {
			Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

			// If the user has any recognized role, redirect to /dashboard
			response.sendRedirect("/dashboard");
		};
	}

	// Add the CommonsMultipartResolver bean @Bean public CommonsMultipartResolver
	// multipartResolver() { CommonsMultipartResolver multipartResolver = new
	// CommonsMultipartResolver(); multipartResolver.setMaxUploadSize(10 * 1024 *
	// 1024); // Set maximum upload size to 10 MB return multipartResolver;

}
