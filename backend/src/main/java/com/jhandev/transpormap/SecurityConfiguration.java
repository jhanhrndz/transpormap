package com.jhandev.transpormap;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfiguration {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/v1/health").permitAll()
                .anyRequest().denyAll());
        http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(
                (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
        return http.build();
    }
}