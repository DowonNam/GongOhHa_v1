package com.korea.basic1.Schedule.Event;

import com.korea.basic1.Schedule.UserCalendar.CalendarRepository;
import com.korea.basic1.Schedule.UserCalendar.UserCalendar;
import com.korea.basic1.Schedule.UserCalendar.CalendarService;
import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CalendarService calendarService;
    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;

    public List<Event> getEventsForMonth(List<Event> allEvents, int targetMonth) {
        // 주어진 이벤트 목록(allEvents)을 스트림으로 변환
        return allEvents.stream()
                // 각 이벤트 객체에 대해 시작일을 가져와서 그 달의 값이 타겟 달과 같은지 확인하는 필터링 조건을 적용
                .filter(event -> {
                    LocalDateTime startDate = event.getStartDate();
                    return startDate != null && startDate.getMonthValue() == targetMonth;
                })
                // 필터링된 이벤트를 리스트로 수집하여 반환
                .collect(Collectors.toList());
    }

    public Event create(String title, LocalDateTime startDate, LocalDateTime endDate, String link, Long calendarId) {
        Event e = new Event();
        e.setTitle(title);
        e.setCreateDate(LocalDateTime.now());
        e.setStartDate(startDate);
        e.setEndDate(endDate);
        e.setRegistrationLink(link);
        UserCalendar userCalendar = calendarService.getcalendar(calendarId);
        e.setUserCalendar(userCalendar);
        return eventRepository.save(e); // 저장된 이벤트 객체 반환
    }


    public Event modify(Long eventId, String title, LocalDateTime startDate, LocalDateTime endDate, String registrationLink, Long calendarId) {
        // 1. 주어진 이벤트 ID를 사용하여 데이터베이스에서 해당 이벤트를 조회합니다.
        Event event = eventRepository.findById(eventId).orElse(null);

        // 2. 조회된 이벤트가 존재하는지 확인합니다.
        if (event == null) {
            // 이벤트가 존재하지 않으면 예외를 발생시킵니다.
            throw new EntityNotFoundException("Event not found with ID: " + eventId);
        }

        // 3. 클라이언트가 전송한 수정된 정보를 사용하여 이벤트를 업데이트합니다.
        // 주로 이벤트의 제목, 시작일, 종료일, 등록 링크 등의 정보가 업데이트됩니다.
        event.setTitle(title);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setRegistrationLink(registrationLink);
        event.setModifyDate(LocalDateTime.now());
        // 다른 필드도 필요에 따라 업데이트할 수 있습니다.

        // 4. 업데이트된 이벤트를 데이터베이스에 저장하고, 업데이트된 이벤트를 반환합니다.
        return eventRepository.save(event);
    }

    public void delete(Event event) {
        this.eventRepository.delete(event);
    }

    public List<Event> findByCalendarId(Long calendarId) {
        return eventRepository.findByUserCalendarId(calendarId);
    }

    public Event findById(Long eventId) {
        // 이벤트 ID를 사용하여 데이터베이스에서 해당 이벤트를 조회합니다.
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventId));
    }

    public Event copyEventToUserCalendar(Long eventId, Long calendarId) {
        Event originalEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 아이디를 찾을 수 없습니다"));

        UserCalendar userCalendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더를 찾을 수 없습니다."));

        Event newEvent = new Event();
        newEvent.setTitle(originalEvent.getTitle());
        newEvent.setCreateDate(LocalDateTime.now());
        newEvent.setModifyDate(LocalDateTime.now());
        newEvent.setStartDate(originalEvent.getStartDate());
        newEvent.setEndDate(originalEvent.getEndDate());
        newEvent.setRegistrationLink(originalEvent.getRegistrationLink());
        newEvent.setUserCalendar(userCalendar);

        eventRepository.save(newEvent);

        return newEvent;
    }

}
