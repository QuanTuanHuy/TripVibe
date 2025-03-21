using PromotionService.Core.Domain.Constant;

namespace PromotionService.Core.Domain.Dto.Response
{
    public class Resource<T>
    {
        public MetaResource Meta { get; set; }
        public T Data { get; set; }

        public static Resource<T> WithMeta(MetaResource meta)
        {
            return new Resource<T>
            {
                Meta = meta
            };
        }

        public static Resource<T> Success(T data)
        {
            return new Resource<T>
            {
                Meta = new MetaResource(1000, "Success"),
                Data = data
            };
        }
    }
}