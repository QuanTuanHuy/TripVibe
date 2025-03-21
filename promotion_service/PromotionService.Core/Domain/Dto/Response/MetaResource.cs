namespace PromotionService.Core.Domain.Dto.Response
{
    public class MetaResource
    {
        public int Code { get; set; }
        public string Message { get; set; }

        public MetaResource(int code, string message)
        {
            Code = code;
            Message = message;
        }
    }
}