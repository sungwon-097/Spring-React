package com.example.server.config.oauth.provider;

import com.example.server.config.oauth.provider.OAuth2UserInfo;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String) ((Map<String, Object>) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>) attributes.get("profile")).get("nickname");
    }
}
