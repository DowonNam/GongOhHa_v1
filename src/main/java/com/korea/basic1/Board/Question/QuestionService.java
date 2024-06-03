package com.korea.basic1.Board.Question;


import com.korea.basic1.Board.Answer.Answer;
import com.korea.basic1.Board.Answer.AnswerRepository;
import com.korea.basic1.Board.Category.Category;
import com.korea.basic1.DataNotFoundException;
import com.korea.basic1.User.User.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Question> getListSorted(int page, String kw, String sort) {
        Pageable pageable = PageRequest.of(page, 10, getSortingCriteria(sort));
        Specification<Question> spec = search(kw);  // 검색 조건 생성
        return questionRepository.findAll(spec, pageable);
    }

    public Page<Question> getList(int page,String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        // CreateDate 말고 ID로 정렬해야 정렬이 제대로 가능
        // TEST 데이터를 넣을 때 생성 시간이 같게 동시에 들어가서 순서가 제멋대로 변경이 됨
        sorts.add(Sort.Order.desc("ID"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            Question currentQuestion = question.get();
            currentQuestion.setHit(currentQuestion.getHit() + 1);  // 조회수 1 증가
            questionRepository.save(currentQuestion);  // 변경된 조회수를 저장
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public Page<Question> getListByCategory(int categoryId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findByCategoryIdOrderByIdDesc(categoryId, pageable);
    }

    public Page<Question> getListByCategoryAndSort(Integer categoryId, int page, String kw, String sort) {
        Pageable pageable = PageRequest.of(page, 10, getSortingCriteria(sort));
        Specification<Question> spec = searchByCategoryAndKeyword(categoryId, kw);  // 카테고리별 및 키워드 검색 조건 생성
        return questionRepository.findAll(spec, pageable);
    }

    // 카테고리별 및 키워드 검색 조건 생성 메서드
    private Specification<Question> searchByCategoryAndKeyword(Integer categoryId, String kw) {
        return (root, query, cb) -> {
            query.distinct(true);  // 중복 제거
            Predicate predicate = cb.conjunction();
            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
            }
            if (kw != null && !kw.isEmpty()) {
                String likePattern = "%" + kw + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(root.get("subject"), likePattern),
                        cb.like(root.get("content"), likePattern),
                        cb.like(root.get("author").get("username"), likePattern),
                        cb.like(root.join("answerList").get("content"), likePattern),
                        cb.like(root.join("answerList").get("author").get("username"), likePattern)
                ));
            }
            return predicate;
        };
    }

    // 정렬 기준에 따른 Sort 객체 생성 메서드
    private Sort getSortingCriteria(String sortKey) {
        switch (sortKey) {
            case "recentAnswer":
                return Sort.by(Sort.Direction.DESC, "lastAnsweredAt");
            case "recentComment":
                return Sort.by(Sort.Direction.DESC, "lastCommentedAt");
            case "popularity":
                return Sort.by(Sort.Direction.DESC, "hit");
            default:
                return Sort.by(Sort.Direction.DESC, "id");
        }
    }
    public void create(String subject, String content, SiteUser author, Category category) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(author);
        q.setCategory(category);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content, Category category) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        question.setCategory(category);
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public List<Question> findByAuthorId(Long authorId) {
        return questionRepository.findByAuthorId(authorId);
    }

}