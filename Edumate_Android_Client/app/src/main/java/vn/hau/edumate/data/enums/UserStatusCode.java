package vn.hau.edumate.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusCode {
    NORMAL("Bình thường", 0),
    WARNING_LOW("Cảnh báo mức 1", 7),
    WARNING_MEDIUM("Cảnh báo mức 2", 15),
    WARNING_HIGH("Cảnh báo mức 3", 30),
    LOCKED("Tài khoản đã bị khóa", 0),
    DELETED("Tài khoản đã bị xóa", 0);

    private final String description;

    private final int durationInDays;
}
