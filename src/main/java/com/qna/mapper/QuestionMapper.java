package com.qna.mapper;

import com.qna.dto.QuestionDto;
import com.qna.entity.Question;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    Question postToEntity(QuestionDto.Post post);

    Question patchToEntity(QuestionDto.Patch patch);

    default QuestionDto.SingleResponse entityToResponse(Question question) {
        return QuestionDto.SingleResponse.of(question);
    }

    default List<QuestionDto.MultiResponse> entityToResponses(List<Question> questions) {
        List<QuestionDto.MultiResponse> list = new ArrayList<QuestionDto.MultiResponse>(questions.size());

        for (Question a : questions) {
            list.add(QuestionDto.MultiResponse.of(a));
        }

        return list;
    }
}
