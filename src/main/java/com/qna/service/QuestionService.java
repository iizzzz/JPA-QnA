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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

        if (member.getRoles() == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_AUTHORIZED);
        }

        question.setMember(member);

        return questionRepository.save(question);
    }

    /* 수정 */
    public Question update(Question question, Member member) {
        // 요구사항 1. 질문을 등록한 회원만 수정 가능 - While 문으로 (해결)
        // 요구사항 2. 질문의 공개여부를 변경할 경우 상태 같이 적용 - (해결)
        // 요구사항 3. 질문을 비공개로 설정할 수 있는건 고객만 가능 - (해결)
        // 요구사항 4. 답변이 등록 될 경우 상태 수정 - Answer 쪽에서 구현 (해결)
        // 요구사항 5-6. 질문에 관리자가 답변을 달 경우 질문의 상태 ANSWERED로 수정 + 관리자만 상태 변경 가능
        //              - Answer 쪽에서 로직 작성 필요
        // 요구사항 7. 질문 DELETE 상태로의 변경은 회원만 가능 - delete 메서드에서 작성
        // 요구사항 8. 질문의 공개 & 비밀글 여부가 바뀌면 답변의 공개 & 비밀글 여부 같이 변경 - answer 쪽에서 작성

        Question findQuestion = findVerifiedQuestion(question.getQuestionId());

        List<String> roles = findQuestion.getMember().getRoles();

        if (roles == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_AUTHORIZED);
        }

        while (Objects.equals(findQuestion.getMember().getMemberId(), member.getMemberId())) {
            if (roles.contains("ADMIN")) {
                throw new BusinessLogicException(ExceptionCode.VALID_AUTHORIZED);
            } else {
                findQuestion.setSecret(question.getSecret());
            }

            if (roles.contains("USER")) {
                findQuestion.setStatus(question.getStatus());
            } else {
                throw new BusinessLogicException(ExceptionCode.VALID_AUTHORIZED);
            }
        }

        findQuestion.update(question.getQuestionId(), question.getTitle(), question.getContent());

        return questionRepository.save(findQuestion);
    }

    /* 한건 조회 */
    @Transactional(readOnly = true)
    public Question find(long questionId, Member member) {
        // 요구사항 1. 질문 조회는 고객, 관리자 모두 조회 가능 - (해결)
        // 요구사항 2. 비밀글 상태의 질문은 고객, 관리자만 조회 가능 - (해결)
        // 요구사항 3. 조회 시, 답변이 존재한다면 답변도 같이 조회 - 해결
        // 요구사항 4. 이미 삭제 상태인 질문 조회 불가능

        Question findQuestion = findVerifiedQuestion(questionId);

        if (findQuestion.getMember().getMemberId() != member.getMemberId()) {
            throw new BusinessLogicException(ExceptionCode.NOT_AUTHORIZED);
        }
        if (findQuestion.getStatus() == QuestionStatus.DELETE) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
        }
        return findQuestion;
    }

    /* 전체 조회 */
    @Transactional(readOnly = true)
    public Page<Question> findAll(int page, int size, Member member) {
        // 요구사항 1. 질문 목록은 회원, 관리자 모두 조회 가능 - (해결)
        // 요구사항 2. 삭제상태가 아닌 질문만 조회 가능 - (임시 해결)
        // 요구사항 3. 질문 목록에서 각각의 질문에 답변이 존재하면 답변 출력
        // 요구사항 4. 여러건의 질문 목록은 페이지네이션 처리가 되어야 한다. - 완료

        Page<Question> questions = questionRepository.findAllByDeletedAtIsNull(PageRequest.of(page, size, Sort.by("questionId").descending()));
//        List<Question> list = questions.getContent();
//
//        for (Question a : list) {
//            if (a.getStatus().equals(QuestionStatus.DELETE)) {
//                continue;
//            }
//        }

        return questions;
    }

    /* 질문 삭제 */
    public void delete(long questionId, Member member) {
        // 요구사항 1. 회원만 삭제 가능 - (해결)
        // 요구사항 2. 질문을 등록한 회원만 삭제 가능 - (해결)
        // 요구사항 3. 질문 삭제 시, 테이블의 Row 변경 X, 상태만 DELETE로 변경 - (해결)
        // 요구사항 4. 이미 삭제 상태인 질문은 삭제 불가능 - (해결)

        Question findQuestion = findVerifiedQuestion(questionId);

        while (Objects.equals(findQuestion.getMember().getMemberId(), member.getMemberId())) {

            if (findQuestion.getDeletedAt() != null) {
                throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
            } else {
                findQuestion.setStatus(QuestionStatus.DELETE);
                findQuestion.setDeletedAt(LocalDateTime.now());
            }
        }
    }

    private Question findVerifiedQuestion(long questionId) {
        Optional<Question> optQuestion = Optional.of(questionRepository.getReferenceById(questionId));

        return optQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }
}
