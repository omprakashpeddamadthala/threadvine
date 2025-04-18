package com.threadvine.mappers;

import com.threadvine.dto.CommentDTO;
import com.threadvine.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping( target = "userId" ,source = "user.id")
    CommentDTO toDto(Comment comment);

    @Mapping( target = "user.id" ,source = "userId")
    @Mapping( target = "product" ,ignore = true)
    Comment toEntity(CommentDTO commentDTO);
}
