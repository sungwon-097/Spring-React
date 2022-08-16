package com.example.server.config;

import com.example.server.config.oauth.PrincipalOauthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security Filter 가 Spring Filter Chain 에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // @Secured, @PreAuthorize, @PostAuthorize 활성화
public class SecurityConfig { // WebSecurityConfigureAdapter 가 Deprecated. @Bean 등록으로 SecurityConfig 구현 가능

    @Autowired
    private PrincipalOauthUserService principalOauthUserService;

    @Bean // 해당 method 의 return object 를 IoC 로 등록해준다
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configureSetting(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // /login 이라는 주소가 호출되면 security 가 낚아채서 대신 로그인을 진행해줌 -> controller 에 /login 을 만들지 않아도
                .defaultSuccessUrl("/") // 특정페이지를 요청한 후에 로그인을 했다면 그 페이지로 보낸다
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인이 완료 된 후의 후처리가 필요함
                .userInfoEndpoint()
                .userService(principalOauthUserService);
        // 1. Code 2. AccessToken(permit) 3. user information
        // 4-1. (Extra information) 4-2. sign up
        return http.build();
    }
}