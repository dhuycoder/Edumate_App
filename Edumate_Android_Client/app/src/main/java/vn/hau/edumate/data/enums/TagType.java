package vn.hau.edumate.data.enums;

import lombok.Getter;

@Getter
public enum TagType {
    SHARING_KNOWLEDGE("Chia sẻ kiến thức"),

    HOMEWORK_SUPPORT("Giải đáp bài tập");
    ;
    private final String description;

    TagType(String description) {
        this.description = description;
    }
}
