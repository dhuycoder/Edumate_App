package vn.hau.edumate.data.model.response;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import vn.hau.edumate.data.enums.TagType;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TagResponse extends AbstractResponse implements Serializable {

    private String name;
    private TagType tagType;


}
