package vn.hau.edumate.data.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class AbstractResponse implements Serializable {
    private Long id;
    String createdBy;
    String updatedBy;

    String createdAt;
    String updatedAt;
}
