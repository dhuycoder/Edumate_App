package vn.hau.edumate.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthMethod {
    FIREBASE_GOOGLE("firebase-google"),
    FIREBASE_FACEBOOK("firebase-facebook"),
    SYSTEM("system");

    private final String method;
}
