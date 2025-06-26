package vn.hau.edumate.data.model.response;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ImageResponse extends AbstractResponse implements Serializable {
    private String imageUrl;
    private String fileName;
    private String fileType;
}
