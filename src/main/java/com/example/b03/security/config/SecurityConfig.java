package com.example.b03.security.config;

import com.example.b03.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/member/register",
                                "/member/check-loginid",
                                "/member/join",
                                "/member/main",
                                "/css/**", "/js/**", "/images/**", "/webjars/**"
                        ).permitAll()  // 특정 경로에 대한 허용
                        .requestMatchers("/main/login").permitAll()  // 로그인 페이지 허용
                        .anyRequest().authenticated()  // 그 외 요청은 인증이 필요
                )
                .formLogin(form -> form
                        .loginPage("/member/login")  // 로그인 페이지 URL
                        .loginProcessingUrl("/member/login")  // 로그인 처리 URL
                        .usernameParameter("loginId")  // 로그인 ID 파라미터명
                        .passwordParameter("password")  // 비밀번호 파라미터명
                        .defaultSuccessUrl("/member/logined", true)  // 로그인 성공 시 이동할 URL
                        .failureUrl("/member/login?error=true")  // 로그인 실패 시 이동할 URL
                        .permitAll()  // 모든 사용자에게 로그인 페이지 접근 허용
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/member/main")
                        .invalidateHttpSession(true)  // 세션 무효화
                        .deleteCookies("JSESSIONID")  // 쿠키 삭제
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 세션이 필요한 경우에만 생성
                        .maximumSessions(1)  // 동시 세션 수 제한
                        .expiredUrl("/login?expired")  // 세션 만료 시 리디렉션 URL
                )
                .authenticationProvider(authenticationProvider());  // 인증 제공자 설정

        return http.build();
    }

}