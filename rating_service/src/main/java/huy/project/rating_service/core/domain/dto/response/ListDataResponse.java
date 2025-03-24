package huy.project.rating_service.core.domain.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListDataResponse<T> {
    private Long totalPage;
    private Long totalRecord;
    private Long pageSize;
    private Long nextPage;
    private Long previousPage;

    private List<T> data;

    public static <T> ListDataResponse<T> from(PageInfo pageInfo, List<T> data) {
        return ListDataResponse.<T>builder()
                .totalPage(pageInfo.getTotalPage())
                .totalRecord(pageInfo.getTotalRecord())
                .pageSize(pageInfo.getPageSize())
                .nextPage(pageInfo.getNextPage())
                .previousPage(pageInfo.getPreviousPage())
                .data(data)
                .build();
    }
}
