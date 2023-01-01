package com.qna.repository;

import com.qna.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT q FROM Question q WHERE q.questionId = :questionId")
    Optional<Question> findByOne(Long questionId);

}
