package com.qna.mapper;

import com.qna.dto.AnswerDto;
import com.qna.entity.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    @Mapping(source = "questionId", target = "question.questionId")
    @Mapping(source = "memberId", target = "member.memberId")
    Answer postToEntity(AnswerDto.Post post);

    Answer patchToEntity(AnswerDto.Patch patch);

    @Mapping(source = "member.memberId", target = "memberId")
    @Mapping(source = "question.questionId", target = "questionId")
    AnswerDto.Response entityToResponse(Answer answer);

    List<AnswerDto.Response> entitysToResponses(List<Answer> answers);
}
