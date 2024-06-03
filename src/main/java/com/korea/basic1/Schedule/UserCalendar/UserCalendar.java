package com.korea.basic1.Schedule.UserCalendar;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.korea.basic1.Schedule.Event.Event;
import com.korea.basic1.User.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class UserCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "userCalendar", cascade = CascadeType.REMOVE)
    private List<Event> eventList;

    private LocalDateTime createDate;

    @OneToOne(mappedBy = "userCalendar")
    private SiteUser siteUser;

}
