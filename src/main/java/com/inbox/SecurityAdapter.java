package com.inbox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityAdapter {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.antMatchers("/").permitAll();
                    auth.antMatchers("/favicon.ico").permitAll();
                    auth.anyRequest().authenticated();
                    
                })
                .oauth2Login(withDefaults())   
                .build();
    }

}

//@Configuration
//public class SecurityAdapter extends WebSecurityConfigurerAdapter {
//
//    @Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// @formatter:off
//		http
//			.authorizeRequests(a -> a
//				.antMatchers("/", "/error").permitAll()
//				.anyRequest().authenticated()
//			)
//			.exceptionHandling(e -> e
//				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//			)
//			.csrf(c -> c
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//			)
//			.logout(l -> l
//				.logoutSuccessUrl("/").permitAll()
//			)
//			.oauth2Login(withDefaults());
//		// @formatter:on
//    }
//    
//}

