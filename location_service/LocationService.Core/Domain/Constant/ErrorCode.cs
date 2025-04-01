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
        PROVINCE_NOT_FOUND = 100006,
        LOCATION_NOT_FOUND = 100007,
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
                ErrorCode.PROVINCE_NOT_FOUND => "Province not found",
                ErrorCode.LOCATION_NOT_FOUND => "Location not found",
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
                ErrorCode.PROVINCE_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.LOCATION_NOT_FOUND => HttpStatusCode.NotFound,
                _ => HttpStatusCode.InternalServerError
            };
        }
    }
}