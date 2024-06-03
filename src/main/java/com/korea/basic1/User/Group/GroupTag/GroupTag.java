package com.korea.basic1.User.Group.GroupTag;

import com.korea.basic1.Tag.Tag;
import com.korea.basic1.User.Group.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GroupTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
}
