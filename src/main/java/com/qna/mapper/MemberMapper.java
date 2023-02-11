package com.qna.mapper;

import com.qna.dto.MemberDto;
import com.qna.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member memberPostDtoToMember(MemberDto.Post requestBody);
    Member memberPatchDtoToMember(MemberDto.Patch requestBody);

    @Mapping(source = "stamp.stampCount", target = "stampCount")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "roles", target = "roles")
    MemberDto.Response memberToMemberResponseDto(Member member);
    List<MemberDto.Response> membersToMemberResponseDtos(List<Member> members);
}
