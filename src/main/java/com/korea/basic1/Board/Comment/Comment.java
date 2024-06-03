package com.korea.basic1.Board.Comment;

import com.korea.basic1.Board.Answer.Answer;
import com.korea.basic1.Board.Question.Question;
import com.korea.basic1.User.User.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    private LocalDateTime lastCommentedAt; // 최근 댓글 시간 필드 추가

    // 생성자, getter 및 setter

    // 댓글이 저장되거나 업데이트될 때마다 최근 댓글 시간을 업데이트하는 메서드
    @PrePersist
    @PreUpdate
    public void updateLastCommentedAt() {
        if(this.question != null){
            this.question.setLastCommentedAt(LocalDateTime.now());
        }

    }
}
