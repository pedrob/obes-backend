package com.obes.backend.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import com.obes.backend.service.UserDetailsServiceImpl;
import static com.obes.backend.config.SecurityConstants.SIGN_UP_URL;

import java.util.Collections;
import static java.util.Arrays.asList;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String SECRET;

    public WebSecurity(UserDetailsServiceImpl userDetailsService, 
            BCryptPasswordEncoder bCryptPasswordEncoder, @Value("{jwt.SECRET}") String SECRET) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.SECRET = SECRET;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.GET, "/books").permitAll()
                .antMatchers(HttpMethod.GET, "/books/{\\d+}").permitAll()
                .antMatchers(HttpMethod.GET, "/books/search").permitAll()
                .antMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), this.SECRET))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), this.SECRET))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
    corsConfiguration.setAllowedMethods(asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
    corsConfiguration.setAllowedHeaders(asList("Authorization", "Cache-Control", "Content-Type", "X-Origem"));
    corsConfiguration.setExposedHeaders(asList("Authorization", "X-Origem"));
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

}