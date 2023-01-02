package com.qna.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 20)
    private Boolean secret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionStatus status = QuestionStatus.REGISTRATION;

    @ManyToOne(optional = false ,fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Answer> answers = new ArrayList<>();

    void addMember(Member member) {
        this.member = member;
        if (!this.member.getQuestions().contains(this))
            this.member.getQuestions().add(this);
    }

    void addAnswer(Answer answer) {
        this.getAnswers().add(answer);
        answer.setQuestion(this);
    }

    public Question(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public enum QuestionStatus {
        REGISTRATION("질문 생성 상태"),
        ANSWERED("답변 완료 상태"),
        DELETE("질문 삭제 상태");

        @Getter
        private String status;

        QuestionStatus(String status) {
            this.status = status;
        }
    }
}
