package com.example.myshop.config;

import com.example.myshop.member.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MemberService memberService;

    public SecurityConfig(@Lazy MemberService memberService) {
        this.memberService = memberService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(memberService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	String [] allowsUri = {"/api/members"
    			, "/api/login"
    			, "/api/products"
    			, "/api/cart/**"
    			, "/api/orders/**"
    			, "/api/logout"
    			, "/api/batch/**"
    	};
    	
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 서버이므로)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(allowsUri).permitAll() // /api/batch/** 경로 허용
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
            )
            .logout(logout -> logout // 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", "GET")) // GET 요청으로 로그아웃 처리
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)) // 200 OK 반환
                .invalidateHttpSession(true) // HTTP 세션 무효화
                .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
            );
        return http.build();
    }
}
