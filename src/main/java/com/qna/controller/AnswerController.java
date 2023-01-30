package com.qna.controller;

import com.qna.dto.AnswerDto;
import com.qna.dto.globalResponse.SingleResponseDto;
import com.qna.entity.Answer;
import com.qna.mapper.AnswerMapper;
import com.qna.service.AnswerService;
import com.qna.service.MemberService;
import com.qna.service.QuestionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerMapper mapper;
    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody AnswerDto.Post post) {
        Answer answer = answerService.create(mapper.postToEntity(post));

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.entityToResponse(answer)), HttpStatus.CREATED);
    }

    @PatchMapping("/{answer-id}")
    public ResponseEntity patch(@PathVariable("answer-id") @Positive long answerId,
                                @Valid @RequestBody AnswerDto.Patch patch) {

        patch.setAnswerId(answerId);

        Answer update = answerService.update(mapper.patchToEntity(patch));

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.entityToResponse(update)), HttpStatus.OK);
    }

    @DeleteMapping("/{answer-id}")
    public ResponseEntity delete(@PathVariable("answer-id") @Positive long answerId) {
        answerService.delete(answerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{answer-id}")
    public ResponseEntity get(@PathVariable("answer-id") @Positive long answerId) {
        Answer answer = answerService.find(answerId);
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.entityToResponse(answer)), HttpStatus.OK);
    }
}
