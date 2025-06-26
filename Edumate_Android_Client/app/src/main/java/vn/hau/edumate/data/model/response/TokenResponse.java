package vn.hau.edumate.data.model.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponse {

    String accessToken;

    String refreshToken;

    long accessTokenExpiresIn;

    long refreshTokenExpiresIn;

}
