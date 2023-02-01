package com.qna.controller;


import com.qna.dto.QuestionDto;
import com.qna.dto.globalResponse.MultiResponseDto;
import com.qna.dto.globalResponse.SingleResponseDto;
import com.qna.entity.Member;
import com.qna.entity.Question;
import com.qna.mapper.QuestionMapper;
import com.qna.service.MemberService;
import com.qna.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.lang.reflect.Field;
import java.util.List;

@RequestMapping("/api/questions")
@RestController
@Validated
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionMapper mapper;
    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponseDto post(@Valid @RequestBody QuestionDto.Post post,
                               @AuthenticationPrincipal Member member) {

        memberService.findVerifiedMember(member.getMemberId());

        Question question = questionService.create(mapper.postToEntity(post), member);

        return new SingleResponseDto<>(mapper.entityToResponse(question));
    }

    @PatchMapping("/{question-id}")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto patch(@PathVariable("question-id") @Positive long questionId,
                                   @Valid @RequestBody QuestionDto.Patch patch,
                                   @AuthenticationPrincipal Member member) {
        memberService.findVerifiedMember(member.getMemberId());

        patch.setQuestionId(questionId);

        Question update = questionService.update(mapper.patchToEntity(patch), member);

        return new SingleResponseDto<>(mapper.entityToResponse(update));
    }

    @GetMapping("/{question-id}")
    @ResponseStatus(HttpStatus.OK)
    public SingleResponseDto get(@PathVariable("question-id") @Positive long questionId,
                                 @AuthenticationPrincipal Member member) {
        memberService.findVerifiedMember(member.getMemberId());

        Question question = questionService.find(questionId, member);

        return new SingleResponseDto<>(mapper.entityToResponse(question));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MultiResponseDto gets(@Positive @RequestParam(defaultValue = "1") int page,
                               @Positive @RequestParam(defaultValue = "10") int size,
                                 @AuthenticationPrincipal Member member) {
        memberService.findVerifiedMember(member.getMemberId());

        Page<Question> pageQuestion = questionService.findAll(page-1, size, member);
        List<Question> questions = pageQuestion.getContent();

        return new MultiResponseDto<>(mapper.entityToResponses(questions), pageQuestion);
    }

    @DeleteMapping("/{question-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("question-id") @Positive long questionId,
                       @AuthenticationPrincipal Member member) {
        memberService.findVerifiedMember(member.getMemberId());

        questionService.delete(questionId, member);
    }

}
