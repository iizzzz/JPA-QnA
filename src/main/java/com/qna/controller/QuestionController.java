package com.qna.controller;


import com.qna.dto.QuestionDto;
import com.qna.dto.globalResponse.MultiResponseDto;
import com.qna.dto.globalResponse.SingleResponseDto;
import com.qna.entity.Question;
import com.qna.mapper.QuestionMapper;
import com.qna.service.MemberService;
import com.qna.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RequestMapping("/api/questions")
@RestController
@Validated
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionMapper mapper;

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody QuestionDto.Post post) {
        Question question = questionService.create(mapper.postToEntity(post));

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.entityToResponse(question)), HttpStatus.CREATED);
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity patch(@PathVariable("question-id") @Positive long questionId,
                                @Valid @RequestBody QuestionDto.Patch patch) {
        patch.setQuestionId(questionId);

        Question update = questionService.update(mapper.patchToEntity(patch));

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.entityToResponse(update)), HttpStatus.OK);
    }

    @GetMapping("/{question-id}")
    public ResponseEntity get(@PathVariable("question-id") @Positive long questionId) {
        Question question = questionService.find(questionId);

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.entityToResponse(question)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity gets(@Positive @RequestParam(defaultValue = "1") int page,
                               @Positive @RequestParam(defaultValue = "10") int size) {
        Page<Question> pageQuestion = questionService.findAll(page-1, size);
        List<Question> questions = pageQuestion.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.entitysToResponses(questions), pageQuestion), HttpStatus.OK);
    }

    @DeleteMapping("/{question-id}")
    public ResponseEntity delete(@PathVariable("question-id") @Positive long questionId) {
        questionService.delete(questionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
