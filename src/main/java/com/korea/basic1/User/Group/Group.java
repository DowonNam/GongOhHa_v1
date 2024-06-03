package com.korea.basic1.User.Group;

import com.korea.basic1.User.Group.GroupTag.GroupTag;
import com.korea.basic1.User.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "`group`")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String goal;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private SiteUser leader;

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<SiteUser> members = new HashSet<>();

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTag> groupTagList = new ArrayList<>();

    @Transient
    private int averageStudyTime;

}
