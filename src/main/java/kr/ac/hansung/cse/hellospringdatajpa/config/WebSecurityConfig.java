package kr.ac.hansung.cse.hellospringdatajpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private static final String[] PUBLIC_MATCHERS = {
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/about/**",
            "/contact/**",
            "/error/**",
            "/console/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_MATCHERS).permitAll()
                        .requestMatchers("/", "/home", "/signup", "/login").permitAll()
                        .requestMatchers("/products/new", "/products/save", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/products", "/products/").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/products")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/accessDenied")
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}