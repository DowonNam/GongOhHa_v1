package com.korea.basic1.Board.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByAuthor_Id(userId);
    }
}
