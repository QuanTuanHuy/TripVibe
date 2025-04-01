namespace LocationService.Core.Domain.Dto.Response;

public class PageInfo
{
    public int totalPage { get; set; }
    public int totalRecord { get; set; }
    public int pageSize { get; set; }
    public int nextPage { get; set; }
    public int previousPage { get; set; }

    public static PageInfo ToPageInfo(int currentPage, int pageSize, int totalRecord)
    {
        int totalPage = totalRecord / pageSize;
        if (totalRecord % pageSize > 0)
        {
            totalPage++;
        }

        return new PageInfo
        {
            totalPage = totalPage,
            totalRecord = totalRecord,
            pageSize = pageSize,
            nextPage = currentPage < totalPage ? currentPage + 1 : totalPage,
            previousPage = currentPage > 0 ? currentPage - 1 : 0
        };
    }
}