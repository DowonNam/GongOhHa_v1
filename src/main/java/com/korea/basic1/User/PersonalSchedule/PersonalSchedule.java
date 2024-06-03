package com.korea.basic1.User.PersonalSchedule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.korea.basic1.User.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PersonalSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private int totalTime; // Total study time in seconds

    private LocalDateTime lastReset;

    @ManyToOne
    @JsonBackReference
    private SiteUser siteUser;

    public int getTodayStudyTime() {
        LocalDateTime now = LocalDateTime.now();
        if (lastReset != null && lastReset.toLocalDate().isEqual(now.toLocalDate())) {
            return totalTime;
        }
        return 0;
    }

}
