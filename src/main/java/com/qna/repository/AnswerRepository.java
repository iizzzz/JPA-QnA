package com.qna.repository;

import com.qna.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM Answer a WHERE a.answerId = :answerId")
    Optional<Answer> findByOne(Long answerId);

//    Optional<List<Answer>> findBy(List<Answer> answers);
}
