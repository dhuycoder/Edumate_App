package vn.hau.edumate.data.model.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FirebaseTokenRequest {

    private String token;
}
