package com.example.server.controller;

import com.example.server.config.auth.PrincipalDetails;
import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication
            , @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("/test/login ==================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : "+principalDetails.getUsername());

        System.out.println("userDetail : "+userDetails.getUsername());
        return "Local login test";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication
            , @AuthenticationPrincipal OAuth2User oAuth){
        System.out.println("/test/oauth/login ==================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : "+oAuth2User.getAttributes());
        System.out.println("oauth2User : "+ oAuth.getAttributes());
        return "OAuth login test";
    }
    @GetMapping({"", "./"})
    public String index(){
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principlaDetails : "+principalDetails.getUsername());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    // 별도의 설정이 없다면 Spring Security 가 해당 주소를 낚아챔
    // SecurityConfig 파일 생성 후 작동 안함
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER");

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user); // 패스워드 암호화가 되지 않았기 때문에 Security 로 로그인을 할 수 없음
        return "redirect:/loginForm"; // redirect 를 하면 loginForm method 를 호출함
    }

    //    @Secured("ROLE_ADMIN") // 단일 권한
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 두 개 이상의 권한
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "private information";
    }
}