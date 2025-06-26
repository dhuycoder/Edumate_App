package vn.hau.edumate.data.model.request;

import lombok.Builder;
import lombok.Data;
import vn.hau.edumate.data.enums.UserStatusCode;

@Data
@Builder
public class CreateUserStatusHistory {
    private UserStatusCode statusCode;
    private String reason;
}
