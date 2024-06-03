package com.korea.basic1.User.PersonalSchedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalScheduleRepository extends JpaRepository<PersonalSchedule,Long> {
    List<PersonalSchedule> findBySiteUserId(Long userId);
}
