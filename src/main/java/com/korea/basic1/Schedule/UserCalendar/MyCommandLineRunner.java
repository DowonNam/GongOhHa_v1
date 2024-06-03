package com.korea.basic1.Schedule.UserCalendar;


import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 5번 ID를 가진 유저 캘린더가 존재하는지 확인
        Optional<UserCalendar> existingCalendar = calendarRepository.findById(5L);
        if (existingCalendar.isEmpty()) {
            // 새로운 유저 생성
            SiteUser user = new SiteUser();
            user.setUsername("defaultUser");
            user.setEmail("D@naver.com");
            user.setPassword("password");
            user.setUserNickname("닉네임");
            userRepository.save(user);

            // 새로운 캘린더 생성
            UserCalendar calendar = new UserCalendar();
            calendar.setId(5L); // 주의: ID를 직접 설정하려면 JPA에서 ID 생성 전략을 따로 관리해야 할 수 있습니다.
            calendar.setCreateDate(LocalDateTime.now());
            calendarRepository.save(calendar);
        }
    }
}