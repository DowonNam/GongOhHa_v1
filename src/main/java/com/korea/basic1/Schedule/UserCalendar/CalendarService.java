package com.korea.basic1.Schedule.UserCalendar;


import com.korea.basic1.DataNotFoundException;
import com.korea.basic1.Schedule.Event.Event;
import com.korea.basic1.Schedule.Event.EventRepository;
import com.korea.basic1.User.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final EventRepository eventRepository;

    public UserCalendar createCalendar(SiteUser user) {
        UserCalendar calendar = new UserCalendar();
        calendar.setCreateDate(LocalDateTime.now());
        calendar.setSiteUser(user);
        UserCalendar savedCalendar = calendarRepository.save(calendar);

        // 기본 이벤트 생성
        createDefaultEvents(savedCalendar);

        return savedCalendar;
    }

    private void createDefaultEvents(UserCalendar calendar) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 특정 날짜와 시간을 설정
        LocalDateTime startDate1 = LocalDateTime.parse("1990-09-09 00:00:00", formatter);
        LocalDateTime endDate1 = startDate1.plusHours(1);

        Event event1 = new Event();
        event1.setTitle("Default Event 1");
        event1.setStartDate(startDate1);
        event1.setEndDate(endDate1);
        event1.setUserCalendar(calendar);
        event1.setCreateDate(LocalDateTime.now());
        eventRepository.save(event1);

        eventRepository.save(event1);
    }

    public void addEvent(Long calendarId, Long eventId){
        Optional<UserCalendar> calendar = calendarRepository.findById(calendarId);
        Optional<Event> event = eventRepository.findById(eventId);

        if(calendar.isPresent() && event.isPresent()){
            UserCalendar targetUserCalendar = calendar.get();
            Event targetEvent = event.get();

            targetEvent.setUserCalendar(targetUserCalendar);
            eventRepository.save(targetEvent);
        }
        else{
            throw new DataNotFoundException("달력이나 일정을 찾을 수 없음");
        }
    }

    public UserCalendar getcalendar(Long id){
        Optional<UserCalendar> calendar = this.calendarRepository.findById(id);
        if (calendar.isPresent()) {
            UserCalendar currentUserCalendar = calendar.get();
            return calendar.get();
        } else {
            throw new DataNotFoundException("달력을 찾을 수 없습니다.");
        }
    }


}