package vn.hau.edumate.data.model.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.hau.edumate.data.enums.AuthMethod;
import vn.hau.edumate.data.enums.UserStatusCode;


@Data
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserResponse  implements Serializable {
    String id;
    String createdBy;
    String updatedBy;
    String createdAt;
    String updatedAt;
    String username;
    String avatarUrl;
    UserStatusCode status;
    String introduction;
    String email;
    String expiredAt;
    AuthMethod authMethod;
    int commentCount;
    int postCount;

}
