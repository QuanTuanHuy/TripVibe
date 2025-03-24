package huy.project.rating_service.kernel.utils;

import huy.project.rating_service.core.domain.dto.request.BaseParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PageUtils {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    public static <T> PageInfo getPageInfo(Page<T> result) {
        var pageInfo = new PageInfo();
        pageInfo.setTotalRecord(result.getTotalElements());
        pageInfo.setTotalPage((long) result.getTotalPages());
        pageInfo.setPageSize((long) result.getSize());
        if (result.hasNext()) {
            pageInfo.setNextPage((long) result.nextPageable().getPageNumber());
        }

        if (result.hasPrevious()) {
            pageInfo.setPreviousPage((long) result.previousPageable().getPageNumber());
        }
        return pageInfo;
    }

    public static Pageable getPageable(BaseParams params) {
        int page = (params.getPage() != null && params.getPage() >= 0) ? params.getPage() : DEFAULT_PAGE;
        int pageSize = (params.getPageSize() != null && params.getPageSize() > 0) ? params.getPageSize() : DEFAULT_PAGE_SIZE;

        // Use "id" as default sort field if not provided
        String sortBy = (!StringUtils.hasText(params.getSortBy()))
                ? "id"
                : params.getSortBy();

        // Use "DESC" as default sort type if not provided, otherwise check case-insensitively.
        Sort.Direction direction = (!StringUtils.hasText(params.getSortType())
                || params.getSortType().equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, pageSize, sort);
    }
}
