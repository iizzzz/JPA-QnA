package com.qna.mapper;

import com.qna.dto.QuestionDto;
import com.qna.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(source = "memberId", target = "member.memberId")
    Question postToEntity(QuestionDto.Post post);

//    @Mapping(source = "memberId", target = "member")
    Question patchToEntity(QuestionDto.Patch patch);

    @Mapping(source = "member.memberId", target = "memberId")
    @Mapping(source = "answers", target = "answers")
    QuestionDto.Response entityToResponse(Question question);

    List<QuestionDto.Response> entitysToResponses(List<Question> questions);
}
