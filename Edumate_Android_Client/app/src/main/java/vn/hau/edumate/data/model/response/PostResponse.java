package vn.hau.edumate.data.model.response;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Getter
public class PostResponse extends AbstractResponse implements Serializable {

    private String title;
    private String content;
    private UserResponse author;
    private TagResponse tag;
    private List<ImageResponse> images;

    private Integer likeCount;

    private Integer commentCount;
    private boolean liked;
}
