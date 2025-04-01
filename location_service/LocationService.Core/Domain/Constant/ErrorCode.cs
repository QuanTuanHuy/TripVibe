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
        CATEGORY_ALREADY_EXISTS = 100008,
        CATEGORY_NOT_FOUND = 100009,
        LANGUAGE_CODE_ALREADY_EXISTS = 100010,
        LANGUAGE_NAME_ALREADY_EXISTS = 100011,
        ATTRACTION_NAME_ALREADY_EXISTS = 100012,
        LANGUAGE_NOT_FOUND = 100013,
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
                ErrorCode.CATEGORY_ALREADY_EXISTS => "Category already exists",
                ErrorCode.CATEGORY_NOT_FOUND => "Category not found",
                ErrorCode.LANGUAGE_CODE_ALREADY_EXISTS => "Language code already exists",
                ErrorCode.LANGUAGE_NAME_ALREADY_EXISTS => "Language name already exists",
                ErrorCode.ATTRACTION_NAME_ALREADY_EXISTS => "Attraction name already exists",
                ErrorCode.LANGUAGE_NOT_FOUND => "Language not found",
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
                ErrorCode.CATEGORY_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                ErrorCode.CATEGORY_NOT_FOUND => HttpStatusCode.NotFound,
                ErrorCode.LANGUAGE_CODE_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                ErrorCode.LANGUAGE_NAME_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                ErrorCode.ATTRACTION_NAME_ALREADY_EXISTS => HttpStatusCode.BadRequest,
                ErrorCode.LANGUAGE_NOT_FOUND => HttpStatusCode.NotFound,
                _ => HttpStatusCode.InternalServerError
            };
        }
    }
}