package com.qna.repository;

import com.qna.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Modifying
    @Query("update Question q set q.views = q.views + 1 where q.questionId = :questionId")
    int updateViews(@Param("questionId") Long questionId);

    @Query(value = "SELECT q FROM Question q WHERE q.questionId = :questionId")
    Optional<Question> findByOne(Long questionId);
}
