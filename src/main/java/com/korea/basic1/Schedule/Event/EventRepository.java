package com.korea.basic1.Schedule.Event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByUserCalendarId(Long calendarId);
}
