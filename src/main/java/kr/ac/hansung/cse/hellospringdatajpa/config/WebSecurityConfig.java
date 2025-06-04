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
                        // 정적 리소스는 모든 사용자 접근 허용
                        .requestMatchers(PUBLIC_MATCHERS).permitAll()
                        // 홈, 로그인, 회원가입은 인증 없이 접근 가능
                        .requestMatchers("/", "/home", "/signup", "/login").permitAll()
                        // 상품 CUD 작업은 ADMIN 권한 필요
                        .requestMatchers("/products/new", "/products/save", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
                        // 관리자 페이지는 ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 상품 목록은 USER 또는 ADMIN 권한 필요
                        .requestMatchers("/products", "/products/").hasAnyRole("USER", "ADMIN")
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")    // 커스텀 로그인 페이지
                        .defaultSuccessUrl("/products") // 로그인 성공 시 이동
                        .failureUrl("/login?error") // 로그인 실패 시 이동
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")  // 로그아웃 성공 시 이동
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/accessDenied")
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}