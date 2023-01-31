package com.qna.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qna.utils.Auditable;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 20)
    private Boolean secret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    public void addMember(Member member) {
        this.member = member;
    }

    public void addQuestion(Question question) {
        this.question = question;
    }
}
