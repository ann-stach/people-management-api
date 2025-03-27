package pl.kurs.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.kurs.security.authentication.UserDetailsServiceImpl;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    SecurityFilterChain appEndpoints(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/import", POST.name())).hasAnyRole("ADMIN", "IMPORTER")
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/people/search/**", POST.name())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/people/**", POST.name())).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/people/**", PATCH.name())).hasRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/positions/**", PATCH.name())).hasAnyRole("ADMIN", "EMPLOYEE")
                                .anyRequest().permitAll())
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsServiceImpl);
        return authBuilder.build();
    }

}
