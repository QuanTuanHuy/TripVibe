using System.Net;

namespace LocationService.Core.Domain.Constant
{
    public enum ErrorCode
    {
        UNCATEGORIZED_EXCEPTION = 100001,
        SUCCESS = 0,
        COUNTRY_NAME_ALREADY_EXISTS = 100002,
        COUNTRY_CODE_ALREADY_EXISTS = 100003,
        COUNTRY_NOT_FOUND = 100004,
        PROVINCE_CODE_ALREADY_EXISTS = 100005,
    }

    public static class ErrorCodeExtensions
    {
        public static string GetMessage(this ErrorCode errorCode)
        {
            return errorCode switch
            {
                ErrorCode.UNCATEGORIZED_EXCEPTION => "Uncategorized error",
                ErrorCode.SUCCESS => "Success",
                ErrorCode.COUNTRY_NAME_ALREADY_EXISTS => "Country name already exists",
                ErrorCode.COUNTRY_CODE_ALREADY_EXISTS => "Country code already exists",
                ErrorCode.COUNTRY_NOT_FOUND => "Country not found",
                ErrorCode.PROVINCE_CODE_ALREADY_EXISTS => "Province code already exists",
                _ => "Unknown error"
            };
        }

        public static HttpStatusCode GetHttpStatusCode(this ErrorCode errorCode)
        {
            return errorCode switch
            {
                ErrorCode.SUCCESS => HttpStatusCode.OK,
                ErrorCode.COUNTRY_NAME_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                ErrorCode.COUNTRY_CODE_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                ErrorCode.COUNTRY_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.PROVINCE_CODE_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                _ => HttpStatusCode.InternalServerError
            };
        }
    }
}