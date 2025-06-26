package vn.hau.edumate.data.model.response;

import java.util.LinkedHashSet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagedResponse<T> {
    private LinkedHashSet<T> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int limit;
}
