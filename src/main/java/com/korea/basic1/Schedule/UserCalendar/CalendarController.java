package com.korea.basic1.Schedule.UserCalendar;


import com.korea.basic1.Schedule.Event.Event;
import com.korea.basic1.Schedule.Event.EventForm;
import com.korea.basic1.Schedule.Event.EventService;
import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Controller
@RequestMapping("/userCalendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final EventService eventService;
    private final UserService userService;


    @GetMapping("/modify/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId) {
        Event event = eventService.findById(eventId);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Event not found.\"}");
        }
    }

    @PutMapping("/modify/{eventId}")
    public ResponseEntity<?> modifyEvent(@PathVariable Long eventId, @RequestBody EventForm eventForm) {
        try {
            Event modifiedEvent = eventService.modify(eventId, eventForm.getTitle(), eventForm.getStartDate(),
                    eventForm.getEndDate(), eventForm.getRegistrationLink(), eventForm.getCalendar_id());
            if (modifiedEvent != null) {
                return ResponseEntity.ok(modifiedEvent);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Event not found.\"}");
            }
        } catch (Exception e) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, "Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Internal server error occurred.\"}");
        }
    }

    @GetMapping("/{userCalendarId}")
    public String viewCalendar(Model model, @PathVariable(name = "userCalendarId") String calendarId,
                               @RequestParam(name = "targetMonth", required = false, defaultValue = "0") int targetMonth,
                               Principal principal) {
        Long parsedCalendarId;
        try {
            parsedCalendarId = Long.parseLong(calendarId);
        } catch (NumberFormatException e) {
            // 예외 처리
            return "errorPage"; // 오류가 발생하면 적절한 에러 페이지로 리다이렉트합니다.
        }

        // 현재 로그인된 사용자의 유저네임을 Principal 객체에서 추출
        String username = principal.getName();

        // 유저네임을 사용하여 사용자 정보 조회
        SiteUser siteUser = this.userService.getUserByUsername(username);

        // 사용자 정보가 null인지 확인
        if (siteUser == null) {
            return "userNotFound"; // 사용자가 존재하지 않을 경우 적절한 처리 필요
        }

        List<Event> events = this.eventService.findByCalendarId(parsedCalendarId);

        // targetMonth가 0 이하면 현재 월의 값을 사용하여 이벤트 목록을 가져옴
        if (targetMonth <= 0) {
            LocalDateTime now = LocalDateTime.now();
            targetMonth = now.getMonthValue();
        }

        int prevMonth = targetMonth - 1;
        int nextMonth = targetMonth + 1;

        List<Event> eventsForMonth = this.eventService.getEventsForMonth(events, targetMonth);

        model.addAttribute("targetMonth", targetMonth);
        model.addAttribute("prevMonth", prevMonth);
        model.addAttribute("nextMonth", nextMonth);
        model.addAttribute("userCalendarId", parsedCalendarId);
        model.addAttribute("eventsForMonth", eventsForMonth); // 이벤트 목록을 모델에 추가
        model.addAttribute("siteUser", siteUser); // siteUser 객체를 모델에 추가
        model.addAttribute("events", events);

        return "calendarForm";
    }

    @GetMapping("/{userCalendarId}/events")
    public ResponseEntity<?> getEventsByCalendarId(@PathVariable(name = "userCalendarId") Long calendarId) {
        System.out.println("Received calendar ID: " + calendarId);
        List<Event> events = eventService.findByCalendarId(calendarId);
        if (events != null && !events.isEmpty()) {
            return ResponseEntity.ok(events);
        } else {
            // JSON 형태의 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"No events found for this calendar.\"}");
        }
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody EventForm eventForm) {
        try {
            Event createdEvent = eventService.create(eventForm.getTitle(), eventForm.getStartDate(),
                    eventForm.getEndDate(), eventForm.getRegistrationLink(), eventForm.getCalendar_id());
            if (createdEvent != null) {
                return ResponseEntity.ok(createdEvent);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Invalid event data provided.\"}");
            }
        } catch (Exception e) {
            // 서버 내부 오류를 로깅
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, "Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Internal server error occurred.\"}");
        }
    }

    @PostMapping("/{eventId}/copy/{calendarId}")
    public ResponseEntity<Event> copyEventToUserCalendar(@PathVariable Long eventId, @PathVariable Long calendarId) {
        Event copiedEvent = eventService.copyEventToUserCalendar(eventId, calendarId);
        return ResponseEntity.ok(copiedEvent);
    }

}