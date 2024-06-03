package com.korea.basic1.Schedule.UserCalendar;

import com.korea.basic1.User.User.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<UserCalendar,Long> {

    Optional<UserCalendar> findBySiteUser(SiteUser user);

}
