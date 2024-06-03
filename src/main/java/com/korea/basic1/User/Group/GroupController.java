package com.korea.basic1.User.Group;

import com.korea.basic1.User.User.SiteUser;
import com.korea.basic1.User.User.UserRepository;
import com.korea.basic1.User.User.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/edit/{groupId}")
    public String editGroup(@PathVariable Long groupId, Model model, Principal principal) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            String username = principal.getName();
            if (!groupService.isLeader(groupId, username)) {
                return "error/403"; // 권한이 부족함을 알리는 적절한 뷰
            }
            model.addAttribute("group", group);
            return "groupEdit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit/{groupId}")
    public String updateGroup(@PathVariable Long groupId, @RequestParam String name, @RequestParam String goal, Principal principal) {
        String username = principal.getName();
        if (!groupService.isLeader(groupId, username)) {
            return "error/403"; // 권한이 부족함을 알리는 적절한 뷰
        }
        groupService.updateGroup(groupId, name, goal);
        return "redirect:/group/detail/" + groupId;
    }


    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestParam String name, Principal principal) {
        Group group = groupService.createGroup(name, principal);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        groupService.addGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public String getAllGroups(Model model, Principal principal) {
        List<Group> groups = groupService.getAllGroups();
        model.addAttribute("groups", groups);

        // 로그인된 사용자 정보 추가
        String username = principal.getName();
        SiteUser user = userService.getUserByUsername(username);
        model.addAttribute("user", user);

        return "groupList_form";
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Group>> getAllGroupsSortedByMembers() {
        List<Group> groups = groupService.getAllGroupsSortedByMembers();
        return ResponseEntity.ok(groups);
    }


    @GetMapping("/ranking")
    public String getGroupRanking(Model model) {
        List<Group> rankedGroups = groupService.getAllGroupsWithAverageStudyTime();
        model.addAttribute("rankedGroups", rankedGroups);
        return "groupRanking";
    }


    @GetMapping("/detail/{groupId}")
    public String getGroupDetail(@PathVariable Long groupId, Model model, Principal principal) {
        Group group = groupService.getGroupWithTodayStudyTimes(groupId);
        String averageStudyTime = groupService.calculateGroupAverageStudyTime(groupId);
        String username = principal.getName();
        boolean isLeader = groupService.isLeader(groupId, username);

        // 그룹 순위 계산
        Map<Long, Integer> groupRankings = groupService.calculateGroupRankings();
        int groupRank = groupRankings.getOrDefault(groupId, -1); // 순위가 없을 경우 -1 반환

        // 멤버들을 가입 날짜와 이름으로 정렬 (null 값을 처리)
        List<SiteUser> sortedMembers = group.getMembers().stream()
                .sorted(Comparator.comparing(SiteUser::getCreateDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SiteUser::getUserNickname, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        model.addAttribute("group", group);
        model.addAttribute("isLeader", isLeader);
        model.addAttribute("sortedMembers", sortedMembers); // 정렬된 멤버 리스트 추가
        model.addAttribute("averageStudyTime", averageStudyTime); // 평균 공부 시간 추가
        model.addAttribute("groupRank", groupRank); // 그룹 순위 추가
        return "groupDetail";
    }


}