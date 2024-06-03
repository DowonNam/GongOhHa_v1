package com.korea.basic1.Board.Category;


import com.korea.basic1.Board.Question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Question> questionList;

    @Column(columnDefinition = "TEXT")
    private String name;

}
