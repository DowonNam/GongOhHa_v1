package com.korea.basic1.User.PersonalSchedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PersonalScheduleController {

    private final PersonalScheduleService personalScheduleService;

    @GetMapping("/userCalendar/{userId}/schedules")
    @ResponseBody
    public List<PersonalSchedule> getSchedulesByUser(@PathVariable Long userId) {
        return personalScheduleService.getSchedulesByUser(userId);
    }

    @PostMapping("/schedules")
    public String addSubject(@RequestParam Long userId, @RequestParam String subject) {
        personalScheduleService.addSubject(userId, subject);
        return "redirect:/user/studyBoard/"+ userId;
    }

    @PostMapping("/schedules/delete")
    public String deleteSubject(@RequestParam Long userId, @RequestParam Long scheduleId) {
        personalScheduleService.deleteSchedule(scheduleId);
        return "redirect:/user/studyBoard/" + userId;
    }

    @PostMapping("/study-time")
    @ResponseBody
    public void recordStudyTime(@RequestParam Long scheduleId, @RequestParam int duration) {
        personalScheduleService.recordStudyTime(scheduleId, duration);
    }
}
