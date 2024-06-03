package com.korea.basic1.Board.Answer;

import com.korea.basic1.Board.Comment.Comment;
import com.korea.basic1.Board.Question.Question;
import com.korea.basic1.User.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    private LocalDateTime lastAnsweredAt; // 최근 답변 시간 필드 추가

    // 생성자, getter 및 setter

    // 답변이 저장되거나 업데이트될 때마다 최근 답변 시간을 업데이트하는 메서드
    @PrePersist
    @PreUpdate
    public void updateQuestionLastAnsweredAt() {
        if (this.question != null) {
            this.question.setLastAnsweredAt(LocalDateTime.now());
        }
    }
}
