package com.korea.basic1.Security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MySocialUser {

    private String sub;
    private String pass;
    private String name;
    private String email;


    public MySocialUser(String sub, String name, String email) {
        this.sub = sub;
        this.name = name;
        this.email = email;
    }
}
