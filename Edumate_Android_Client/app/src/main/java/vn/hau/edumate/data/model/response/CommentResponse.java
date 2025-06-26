package vn.hau.edumate.data.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CommentResponse  extends  AbstractResponse{
    String content;
    Integer likes;
    Integer commentCount;
    ImageResponse image;
    UserResponse user;
    CommentResponse parent;
    Integer repliesCount;
    PostResponse post;
    boolean liked;

}
