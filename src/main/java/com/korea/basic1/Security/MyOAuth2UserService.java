package com.korea.basic1.Security;

import com.korea.basic1.Schedule.UserCalendar.CalendarService;
import com.korea.basic1.Schedule.UserCalendar.UserCalendar;
import com.korea.basic1.User.PersonalSchedule.PersonalScheduleRepository;
import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final CalendarService calendarService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User user = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        MySocialUser mySocialUser;

        switch (registrationId) {
            case "google" -> mySocialUser = googleService(user);
            case "kakao" -> mySocialUser = kakaoService(user);
            case "naver" -> mySocialUser = naverService(user);
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        }

        SiteUser siteUser = userRepository.findByUsername(mySocialUser.getSub()).orElse(null);

        if(siteUser==null){
            siteUser = new SiteUser();

            siteUser.setUsername(mySocialUser.getSub());
            siteUser.setPassword(mySocialUser.getPass());
            siteUser.setUserNickname(mySocialUser.getName());
            siteUser.setEmail(mySocialUser.getEmail());
            siteUser.setCreateDate(LocalDateTime.now());

            // 새 사용자 저장
            siteUser = userRepository.save(siteUser);

            // 새 사용자 등록 시 달력 생성 및 설정
            UserCalendar userCalendar = calendarService.createCalendar(siteUser);
            siteUser.setUserCalendar(userCalendar);

            // 업데이트된 사용자 저장
            userRepository.save(siteUser);
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        List<SimpleGrantedAuthority> authorities = List.of(authority);
        return new MyUser(siteUser, authorities);
    }

    public MySocialUser googleService(OAuth2User user) {
        String sub = user.getAttribute("sub");
        String pass = "";
        String name = user.getAttribute("name");
        String email = user.getAttribute("email");

        return new MySocialUser(sub, name, email);
    }

    public MySocialUser kakaoService(OAuth2User user) {
        String sub = user.getAttribute("id").toString();
        String pass = "";

        Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
        Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");
        String name = (String)profile.get("nickname");

        String email = (String)kakaoAccount.get("email");

        return new MySocialUser(sub, name, email);
    }

    public MySocialUser naverService(OAuth2User user) {
        Map<String, Object> response = user.getAttribute("response");
        String sub = response.get("id").toString();
        String pass = "";
        String name = response.get("name").toString();
        String email = response.get("email").toString();

        return new MySocialUser(sub, name, email);
    }
}

