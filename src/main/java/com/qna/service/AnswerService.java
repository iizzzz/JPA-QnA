package com.qna.service;

import com.qna.entity.Answer;
import com.qna.entity.Member;
import com.qna.entity.Question;
import com.qna.error.BusinessLogicException;
import com.qna.error.ExceptionCode;
import com.qna.mapper.AnswerMapper;
import com.qna.repository.AnswerRepository;
import com.qna.repository.QuestionRepository;
import com.qna.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {

    //----------------------------- DI ---------------------------------

    private final AnswerRepository answerRepository;
    private final MemberService memberService;
    private final QuestionService questionService;
    //----------------------------- DI ---------------------------------

    /* 생성 */
    public Answer create(Answer answer) {

        // Post DTO 에서 Answer 엔티티로 넘어와서 매핑된 memberId, questionId를 이용해 멤버,질문을 답변에 지정
        Member member = memberService.findMember(answer.getMember().getMemberId());
        Question question = questionService.find(answer.getQuestion().getQuestionId());
        Answer createAnswer = Answer.builder()
                .content(answer.getContent())
                .secret(answer.getQuestion().getSecret())
                .member(member)
                .question(question)
                .build();


        // 요구사항 1. 관리자만 등록 가능 (Security 미구현)

        // 요구사항 2. 답변 등록 시 답변 등록 날짜 생성 - Auditable 로 해결

        // 요구사항 3. 답변이 등록되면 질문의 상태값 ANSWERED 로 변경 - 해결

        // 요구사항 4. 질문의 공개&비밀 글 여부에 따라 답변도 똑같이 상태 변경
        if (createAnswer.getQuestion().getSecret() == Boolean.FALSE) {
            createAnswer.setSecret(Boolean.FALSE);
        } else if (createAnswer.getQuestion().getSecret() == Boolean.TRUE) {
            createAnswer.setSecret(Boolean.TRUE);
        }

        return answerRepository.save(createAnswer);
    }

    /* 수정 */
    public Answer update(Answer answer) {

        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());
        Question findQuestion = questionService.find(findAnswer.getQuestion().getQuestionId());

//        // 요구사항 1. 관리자만 등록 - (Security 미구현)

        Optional.ofNullable(answer.getContent())
                .ifPresent(findAnswer::setContent);

        List<Answer> answerList = findQuestion.getAnswers();

        for (Answer newAnswer : answerList) {
            if (findQuestion.getSecret() == Boolean.TRUE){
                findAnswer.setSecret(Boolean.TRUE);
            } else {
                findAnswer.setSecret(Boolean.FALSE);
            }

            // 답변 생성요구사항 3. 답변이 등록되면 질문의 상태값 ANSWERED 로 변경 - 해결
            if (!findQuestion.getAnswers().isEmpty()) {
                findQuestion.setStatus(Question.QuestionStatus.ANSWERED);
            }
        }

        return answerRepository.save(findAnswer);
    }

    /* 조회 */
    @Transactional(readOnly = true)
    public Answer find(long answerId) {
        Answer answer = answerRepository.getReferenceById(answerId);
        return answer;
    }

    /* 삭제 */
    public void delete(long answerId) {

        // 요구사항 1. 답변 삭제 시, 테이블에서 Row 완전 삭제 - 해결
        answerRepository.deleteById(answerId);
    }

    private Answer findVerifiedAnswer(long answerId) {
        Optional<Answer> optAnswer = answerRepository.findByOne(answerId);
        Answer findAnswer = optAnswer.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));

        return findAnswer;
    }
}
