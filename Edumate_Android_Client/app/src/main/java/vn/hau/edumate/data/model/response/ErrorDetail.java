package vn.hau.edumate.data.model.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorDetail {
    private String field;
    private String message;
    private Object rejectedValue;
}