package vn.hau.edumate.data.model.response;

import java.util.logging.Level;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatbotResponse {
    String answer;
}
