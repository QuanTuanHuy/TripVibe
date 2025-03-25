using System.Net;

namespace PromotionService.Core.Domain.Constant
{
    public enum ErrorCode
    {
        UNCATEGORIZED_EXCEPTION = 100001,
        SUCCESS = 0,
        PROMOTION_TYPE_NAME_EXIST = 100002,
        PROMOTION_TYPE_NOT_FOUND = 100003,
        PROMOTION_NOT_FOUND = 100004,
        GENERAL_FORBIDDEN = 100005,
        PROMOTION_CONDITION_NOT_FOUND = 100006,
        PROMOTION_CONDITION_NAME_EXIST = 100007,
        INVALID_TIME_RANGE = 100008
    }

    public static class ErrorCodeExtensions
    {
        public static string GetMessage(this ErrorCode errorCode)
        {
            return errorCode switch
            {
                ErrorCode.UNCATEGORIZED_EXCEPTION => "Uncategorized error",
                ErrorCode.SUCCESS => "Success",
                ErrorCode.PROMOTION_TYPE_NAME_EXIST => "Promotion type name existed",
                ErrorCode.PROMOTION_TYPE_NOT_FOUND => "Promotion type not found",
                ErrorCode.PROMOTION_NOT_FOUND => "Promotion not found",
                ErrorCode.PROMOTION_CONDITION_NOT_FOUND => "Promotion condition not found",
                ErrorCode.PROMOTION_CONDITION_NAME_EXIST => "Promotion condition name existed",
                ErrorCode.GENERAL_FORBIDDEN => "forbidden",
                _ => "Unknown error"
            };
        }

        public static HttpStatusCode GetHttpStatusCode(this ErrorCode errorCode)
        {
            return errorCode switch
            {
                ErrorCode.SUCCESS => HttpStatusCode.OK,
                ErrorCode.PROMOTION_TYPE_NAME_EXIST => HttpStatusCode.BadRequest,
                ErrorCode.PROMOTION_TYPE_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.GENERAL_FORBIDDEN => HttpStatusCode.Forbidden,
                ErrorCode.PROMOTION_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.PROMOTION_CONDITION_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.PROMOTION_CONDITION_NAME_EXIST => HttpStatusCode.BadRequest,
                _ => HttpStatusCode.InternalServerError
            };
        }
    }
}