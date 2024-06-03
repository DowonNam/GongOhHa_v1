package com.korea.basic1.User.Group;

import com.korea.basic1.User.PersonalSchedule.PersonalSchedule;
import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;


    public Map<Long, Integer> calculateGroupRankings() {
        List<Group> groups = getAllGroupsWithAverageStudyTime();
        Map<Long, Integer> groupRankings = new HashMap<>();
        int rank = 1;
        int previousAverageStudyTime = -1;
        int groupsWithSameRank = 0;

        for (Group group : groups) {
            int currentAverageStudyTime = group.getAverageStudyTime();
            if (currentAverageStudyTime != previousAverageStudyTime) {
                rank += groupsWithSameRank;
                groupsWithSameRank = 1;
            } else {
                groupsWithSameRank++;
            }
            groupRankings.put(group.getId(), rank);
            previousAverageStudyTime = currentAverageStudyTime;
        }
        return groupRankings;
    }

    public List<Group> getAllGroupsWithAverageStudyTime() {
        List<Group> groups = groupRepository.findAll();

        for (Group group : groups) {
            int totalStudyTime = group.getMembers().stream()
                    .mapToInt(member -> {
                        return member.getPersonalSchedules().stream()
                                .mapToInt(PersonalSchedule::getTodayStudyTime)
                                .sum();
                    })
                    .sum();
            int memberCount = group.getMembers().size();
            int averageStudyTime = memberCount > 0 ? totalStudyTime / memberCount : 0;
            group.setAverageStudyTime(averageStudyTime);
        }

        return groups.stream()
                .sorted((g1, g2) -> Integer.compare(g2.getAverageStudyTime(), g1.getAverageStudyTime()))
                .collect(Collectors.toList());
    }

    public String formatSecondsToHMS(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String calculateGroupAverageStudyTime(Long groupId) {
        Group group = getGroupWithTodayStudyTimes(groupId);
        int totalStudyTime = group.getMembers().stream()
                .mapToInt(SiteUser::getTodayStudyTime)
                .sum();
        int memberCount = group.getMembers().size();
        int averageStudyTime = memberCount > 0 ? totalStudyTime / memberCount : 0;

        return formatSecondsToHMS(averageStudyTime);
    }

    public Group getGroupWithTodayStudyTimes(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        for (SiteUser member : group.getMembers()) {
            int todayStudyTime = member.getPersonalSchedules().stream()
                    .mapToInt(PersonalSchedule::getTodayStudyTime)
                    .sum();
            member.setTodayStudyTime(todayStudyTime);
        }

        return group;
    }

    // GroupService에 추가
    public boolean isLeader(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        return group.getLeader().getUsername().equals(username);
    }

    public Group createGroup(String name, Principal principal) {
        String username = principal.getName();
        SiteUser leader = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 유저입니다."));

        Group group = new Group();
        group.setName(name);
        group.setLeader(leader);
        group.setCreateDate(LocalDateTime.now());

        // 그룹에 리더를 멤버로 추가
        group.getMembers().add(leader);

        return groupRepository.save(group);
    }


    public void addGroup(Long groupId, Long userId){
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        Optional<SiteUser> userOpt = userRepository.findById(userId);

        if(groupOpt.isPresent()&&userOpt.isPresent()){
            Group group = groupOpt.get();
            SiteUser user = userOpt.get();
            group.getMembers().add(user);
            groupRepository.save(group);
        } else{
            throw new IllegalArgumentException("그룹이나 유저를 찾을 수 없습니다.");
        }
    }
    // 그룹 업데이트 메소드 추가
    public Group updateGroup(Long groupId, String name, String goal) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        group.setName(name);
        group.setGoal(goal);
        return groupRepository.save(group);
    }

    public Group getGroup(Long id) {
        return groupRepository.findById(id).orElseThrow();
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<Group> getAllGroupsSortedByMembers() {
        return groupRepository.findAllOrderByMembersCountDesc();
    }

    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        groupRepository.delete(group);
    }
}
