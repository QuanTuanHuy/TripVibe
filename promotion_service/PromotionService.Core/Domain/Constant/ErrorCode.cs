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
        INVALID_TIME_RANGE = 100008,
        CONDITION_NOT_FOUND = 100009,
        CONDITION_NAME_EXIST = 100010,
        ACCOMMODATION_NOT_FOUND = 100011,
        UNIT_NOT_FOUND = 100012,
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
                ErrorCode.INVALID_TIME_RANGE => "Invalid time range",
                ErrorCode.CONDITION_NOT_FOUND => "Condition not found",
                ErrorCode.CONDITION_NAME_EXIST => "Condition name existed",
                ErrorCode.GENERAL_FORBIDDEN => "forbidden",
                ErrorCode.ACCOMMODATION_NOT_FOUND => "Accommodation not found",
                ErrorCode.UNIT_NOT_FOUND => "Unit not found",
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
                ErrorCode.INVALID_TIME_RANGE => HttpStatusCode.BadRequest,
                ErrorCode.CONDITION_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.CONDITION_NAME_EXIST => HttpStatusCode.BadRequest,
                ErrorCode.ACCOMMODATION_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.UNIT_NOT_FOUND => HttpStatusCode.NotFound,
                _ => HttpStatusCode.InternalServerError
            };
        }
    }
}