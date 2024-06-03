package com.korea.basic1.Security;

import com.korea.basic1.User.User.SiteUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class MyUser implements OAuth2User, UserDetails {

    private final String username;
    private final String userNickname;
    private final String password;
    private final String base64EncodedProfileImage; // 새로운 속성 추가
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes = new HashMap<>();

    MyUser(SiteUser user, Collection<? extends GrantedAuthority> authorities) {
        this.username = user.getUsername();
        this.userNickname = user.getUserNickname();
        this.password = user.getPassword();
        this.base64EncodedProfileImage = user.getBase64EncodedProfileImage(); // SiteUser에서 해당 값 받아오기
        this.authorities = authorities;
        attributes.put("userNickname", userNickname);
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return userNickname;
    }

}
