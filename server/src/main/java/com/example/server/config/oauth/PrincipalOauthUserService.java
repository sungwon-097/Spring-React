package com.example.server.config.oauth;

import com.example.server.config.auth.PrincipalDetails;
import com.example.server.config.oauth.provider.*;
import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauthUserService extends DefaultOAuth2UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration : "+ userRequest.getClientRegistration());
        System.out.println("getAccessToken : "+ userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 로그인 버튼 => 구글 로그인창 => 로그인 완료 => code(OAuth-Client) => AccessToken 요청
        // userRequest => loadUser => 구글로부터 회원 프로필 받아줌
        System.out.println("getAttribute : "+ super.loadUser(userRequest).getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();
        if(provider.equals("google")){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        else if(provider.equals("facebook")){
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }
        else if(provider.equals("naver")){
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }
        else if(provider.equals("kakao")) oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        else{
            System.out.println("Err");
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId; // google_"sub_ID"
        String email = oAuth2UserInfo.getEmail();
        String password = bCryptPasswordEncoder.encode("password");
        String role = "ROLE_USER";

        // 회원가입을 강제로 진행
        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
