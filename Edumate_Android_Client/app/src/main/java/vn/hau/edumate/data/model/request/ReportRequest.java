package vn.hau.edumate.data.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportRequest {
    private String reason;
}
