package com.threadvine.mappers;

import com.threadvine.dto.CommentDTO;
import com.threadvine.model.Comment;
import com.threadvine.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping( target = "userId" ,source = "user.id")
    CommentDTO toDto(Comment comment);

    @Mapping( target = "user" ,source = "userId", qualifiedByName = "mapUserIdToUser")
    @Mapping( target = "product" ,ignore = true)
    Comment toEntity(CommentDTO commentDTO);


    @Named("mapUserIdToUser")
    default User mapUserIdToUser(UUID userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
