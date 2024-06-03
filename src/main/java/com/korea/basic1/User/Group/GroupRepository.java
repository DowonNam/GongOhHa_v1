package com.korea.basic1.User.Group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group,Long> {

    @Query("SELECT g FROM Group g ORDER BY SIZE(g.members) DESC")
    List<Group> findAllOrderByMembersCountDesc();
}
