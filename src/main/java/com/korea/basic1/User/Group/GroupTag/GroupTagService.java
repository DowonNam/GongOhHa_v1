package com.korea.basic1.User.Group.GroupTag;

import com.korea.basic1.Tag.Tag;
import com.korea.basic1.Tag.TagService;
import com.korea.basic1.User.Group.Group;
import com.korea.basic1.User.Group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupTagService {
    private final GroupTagRepository groupTagRepository;
    private final GroupService groupService;
    private final TagService tagService;

    public GroupTag getGroupTag(Long GroupTagId) {
        return groupTagRepository.findById(GroupTagId).orElseThrow();
    }

    public GroupTag create(Long groupId, String name) {
        Group group = groupService.getGroup(groupId);
        Tag tag = tagService.create(name);

        GroupTag groupTag = new GroupTag();
        groupTag.setGroup(group);
        groupTag.setTag(tag);

        return groupTagRepository.save(groupTag);
    }

    public void delete(Long noteTagId) {
        groupTagRepository.deleteById(noteTagId);
    }


}
