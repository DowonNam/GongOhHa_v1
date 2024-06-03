package com.korea.basic1.Security;



import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SiteUser siteUser = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("존재하지 않는 아이디입니다.")
        );

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        List<SimpleGrantedAuthority> authorities = List.of(authority);

        return new MyUser(siteUser, authorities);
    }
}
