package com.korea.basic1.User.User;


import com.korea.basic1.Board.Answer.Answer;
import com.korea.basic1.Board.Answer.AnswerService;
import com.korea.basic1.Board.Comment.Comment;
import com.korea.basic1.Board.Comment.CommentService;
import com.korea.basic1.Board.Question.Question;
import com.korea.basic1.Board.Question.QuestionService;
import com.korea.basic1.DataNotFoundException;
import com.korea.basic1.Security.MyUser;
import com.korea.basic1.User.PersonalSchedule.PersonalSchedule;
import com.korea.basic1.User.PersonalSchedule.PersonalScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final CommentService commentService;
    private final AnswerService answerService;
    private final PersonalScheduleService personalScheduleService;
    private final UserRepository userRepository;

    @GetMapping("/calendarPersonalFragment")
    public String getCalendarPersonalFragment(Model model) {
        // 필요한 모델 데이터를 추가합니다.
        model.addAttribute("userCalendarId", "sampleCalendarId");
        model.addAttribute("userId", "sampleUserId");
        return "fragments :: calendarPersonalFragment"; // fragments.html 내의 calendarPersonalFragment 부분을 반환
    }

    @GetMapping("/profileModify")
    public String profileModify(ProfileModifyForm profileModifyForm) {
        return "profileModify_form";
    }

    @PostMapping("/profileModify")
    public String profileModify(@Valid ProfileModifyForm profileModifyForm,
                                BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "profileModify_form";
        }
        try {
            String username = principal.getName(); // 현재 로그인한 사용자의 username
            SiteUser user = this.userService.getUser(username);
            String newNickname = profileModifyForm.getUserNickname();
            String newEmail = profileModifyForm.getEmail();
            MultipartFile profileImage = profileModifyForm.getProfileImage(); // 프로필 이미지 가져오기

            // 이메일이 비어있으면 기존 이메일로 설정
            if (newEmail == null || newEmail.isEmpty()) {
                newEmail = user.getEmail();
            }

            if (newNickname == null || newNickname.isEmpty()){
                newNickname = user.getUserNickname();
            }

            this.userService.modifyProfile(user, newNickname, newEmail, profileImage); // 프로필 이미지도 함께 수정
            return String.format("redirect:/user/profile/%s", username);
        } catch (DataNotFoundException e) {
            model.addAttribute("error", "사용자가 존재하지 않습니다.");
            return "profileModify_form";
        }
    }
    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SiteUser user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "userProfile_form";
    }

    @GetMapping("/studyBoard/{username}")
    public String userBoard(Model model, Principal principal) {
        // 현재 로그인된 사용자의 유저네임을 Principal 객체에서 추출
        String username = principal.getName();

        // 유저네임을 사용하여 사용자 정보 조회
        SiteUser user = this.userService.getUserByUsername(username);

        // 사용자 정보가 null인지 확인
        if (user == null) {
            return "userNotFound"; // 사용자가 존재하지 않을 경우 적절한 처리 필요
        }

        // userCalendarId를 올바르게 추출
        Long userCalendarId = user.getUserCalendar().getId();

        // 사용자 개인 일정 조회
        List<PersonalSchedule> schedules = personalScheduleService.getSchedulesByUser(user.getId());
        if (schedules == null || schedules.isEmpty()) {
            System.out.println("No schedules found for userId: " + user.getId());
        } else {
            System.out.println("Schedules found: " + schedules);
        }

        // 모델에 사용자 정보 추가
        model.addAttribute("user", user);
        List<Question> questions = questionService.findByAuthorId(user.getId());
        List<Comment> comments = commentService.findByUserId(user.getId());
        List<Answer> answers = answerService.findByAuthorId(user.getId());

        model.addAttribute("questions", questions);
        model.addAttribute("comments", comments);
        model.addAttribute("answers", answers);
        model.addAttribute("userNickname", user.getUserNickname());
        model.addAttribute("userId", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("userCalendarId", userCalendarId);
        model.addAttribute("schedules", schedules); // 개인 일정 추가

        // 프로필 페이지 뷰 이름 반환
        return "studyBoard";
    }
    @GetMapping("/profile/{userNickname}")
    public String userProfile(Model model, Authentication authentication) {
        // 현재 로그인된 사용자의 유저네임을 Principal 객체에서 추출
        MyUser user = (MyUser) authentication.getPrincipal();

        // 유저네임을 사용하여 사용자 정보 조회
        SiteUser foundedUser = this.userService.getUserByUsername(user.getUsername());

        // 사용자 정보가 null인지 확인
        if (foundedUser == null) {
            return "userNotFound"; // 사용자가 존재하지 않을 경우 적절한 처리 필요
        }

        // 모델에 사용자 정보 추가
        model.addAttribute("user", user);
        List<Question> questions = questionService.findByAuthorId(foundedUser.getId());
        List<Comment> comments = commentService.findByUserId(foundedUser.getId());
        List<Answer> answers = answerService.findByAuthorId(foundedUser.getId());

        model.addAttribute("questions", questions);
        model.addAttribute("comments", comments);
        model.addAttribute("answers", answers);
        model.addAttribute("userNickname",foundedUser.getUserNickname());
        model.addAttribute("userId", foundedUser.getId());
        model.addAttribute("username", foundedUser.getUsername());
        model.addAttribute("email", foundedUser.getEmail());
        model.addAttribute("userCalendarId", foundedUser.getUserCalendar().getId());

        // 프로필 페이지 뷰 이름 반환
        return "userProfile_form";
    }

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getUsernickname());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        // 불필요한 userService.create 호출을 제거함
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

}


