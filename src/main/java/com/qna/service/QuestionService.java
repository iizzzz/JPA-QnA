package com.qna.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qna.entity.Answer;
import com.qna.entity.Member;
import com.qna.entity.Question;
import com.qna.entity.contant.QuestionStatus;
import com.qna.error.BusinessLogicException;
import com.qna.error.ExceptionCode;
import com.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {

    //----------------------------- DI ---------------------------------
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    //----------------------------- DI ---------------------------------


    /* 생성 - 완료 */
    public Question create(Question question, Member member) {
        // 요구사항 1. 질문은 회원만 등록 가능 - 해결
        // 요구사항 2. 질문 등록시 날짜 생성 -> Auditable을 상속받음 - 해결
        // 요구사항 3-4. 질문의 상태값 필요 -> 질문 생성 시, 기본값을 REGISTRATION으로 설정 - 해결
        // 요구사항 5. 제목과 내용은 필수 입력 -> DTO <-> Entity 매핑으로 - 해결
        // 요구사항 6. 공개,비밀글 설정값을 DTO에서 받아와서 객체에 삽입 - 해결

        List<String> roles = member.getRoles();

        if (roles == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_AUTHORIZED);
        }

        question.setMember(member);

        return questionRepository.save(question);
    }

    /* 수정 */
    public Question update(Question question) {
        // 요구사항 1. 질문은 회원만 수정 가능 (Security 미적용으로 부분 적용) - 해결
        // 요구사항 2. 질문의 공개여부를 변경할 경우 상태 적용 - 해결
        // 요구사항 3. 질문을 비공개로 설정할 수 있는건 고객만 가능 - 해결
        // 요구사항 4. 답변이 등록 될 경우 상태 수정 - Answer 쪽에서 구현
        // 요구사항 5-6. 괸리자가 답변을 달 경우 질문의 상태 ANSWERED로 수정 + 관리자만 상태 변경 가능
        // 요구사항 7. 질문 DELETE 상태로의 변경은 회원만 가능
        // 질문의 공개 & 비밀글 여부가 바뀌면 답변의 공개 & 비밀글 여부 같이 변경 - 해결

        Question findQuestion = findVerifiedQuestion(question.getQuestionId());

        List<String> roles = findQuestion.getMember().getRoles();

        if (roles == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_AUTHORIZED);
        }

        if (roles.contains("ADMIN")) {
            findQuestion.setSecret(question.getSecret());
        } else {
            throw new BusinessLogicException(ExceptionCode.VALID_AUTHORIZED);
        }

        if (roles.contains("USER")) {
            findQuestion.setStatus(question.getStatus());
        } else {
            throw new BusinessLogicException(ExceptionCode.VALID_AUTHORIZED);
        }

        findQuestion.update(question.getQuestionId(), question.getTitle(), question.getContent());

        return questionRepository.save(findQuestion);
    }

    /* 한건 조회 */
    @Transactional(readOnly = true)
    public Question find(long questionId) {
        // 요구사항 1. 질문 조회는 고객, 관리자 모두 조회 가능 (관리자 부분 Security 미구현)
        Question findQuestion = findVerifiedQuestion(questionId);
        Member findMember = memberService.findVerifiedMember(findQuestion.getMember().getMemberId());
        findQuestion.setMember(findMember);


//        if (!findQuestion.getMember().equals(findMember)) {
//
//        }

        // 요구사항 2. 비밀글 상태의 질문은 고객, 관리자만 조회 가능
//        if (findQuestion.getSecret() == Boolean.FALSE) {
//            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_AUTHORIZED);
//        }

        // 요구사항 3. 조회 시, 답변이 존재한다면 답변도 같이 조회 - 해결

        // 요구사항 4. 이미 삭제 상태인 질문 조회 불가능
        if (findQuestion.getStatus() == Question.QuestionStatus.DELETE) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
        } else {
            return findQuestion;
        }

//        } else if (findQuestion.getQuestionStatus() == Question.QuestionStatus.REGISTRATION || findQuestion.getQuestionStatus() == Question.QuestionStatus.ANSWERED){
//            return findQuestion;

    }

    /* 전체 조회 */
    @Transactional(readOnly = true)
    public Page<Question> findAll(int page, int size) {
        Page<Question> questions = questionRepository.findAll(PageRequest.of(page, size, Sort.by("questionId").descending()));

        // 요구사항 1. 질문 목록은 회원, 관리자 모두 조회 가능

        // 요구사항 2. 삭제상태가 아닌 질문만 조회 가능
        if (questions.getContent().equals(Question.QuestionStatus.DELETE)) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
        } else {
            return questions;
        }

        // 요구사항 3. 질문 목록에서 각각의 질문에 답변이 존재하면 답변 출력
//        List<Answer> answers = questions.get;

        // 요구사항 4. 여러건의 질문 목록은 페이지네이션 처리가 되어야 한다. - 완료
    }

    /* 질문 삭제 */
    public void delete(long questionId) {
        // 요구사항 1. 회원만 삭제 가능
        Question question = findVerifiedQuestion(questionId);
        Member member = memberService.findVerifiedMember(question.getMember().getMemberId());

        // 요구사항 2. 질문을 등록한 회원만 삭제 가능
        question.setMember(member);

        // 요구사항 3. 질문 삭제 시, 테이블의 Row 변경 X, 상태만 DELETE로 변경
        // 요구사항 4. 이미 삭제 상태인 질문은 삭제 불가능
        if (question.getStatus() == Question.QuestionStatus.DELETE) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
        } else {
            question.setStatus(Question.QuestionStatus.DELETE);
        }
    }

    private Question findVerifiedQuestion(long questionId) {
        Optional<Question> optQuestion = Optional.of(questionRepository.getReferenceById(questionId));

        return optQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }
}
