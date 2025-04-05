using System.Text.Json;
using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Dto.Response;
using LocationService.Core.Exception;

namespace LocationService.UI.Middleware
{
    public class ExceptionHandlingMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<ExceptionHandlingMiddleware> _logger;

        public ExceptionHandlingMiddleware(RequestDelegate next, ILogger<ExceptionHandlingMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext httpContext)
        {
            try
            {
                await _next(httpContext);
            }
            catch (AppException ex)
            {
                await HandleAppExceptionAsync(httpContext, ex);
            }
            catch (Exception ex)
            {
                await HandleExceptionAsync(httpContext, ex);
            }
        }

        private async Task HandleAppExceptionAsync(HttpContext context, AppException exception)
        {
            _logger.LogWarning("Application exception: {Message}", exception.Message);
            
            ErrorCode errorCode = exception.ErrorCode;
            var statusCode = errorCode.GetHttpStatusCode();
            
            context.Response.ContentType = "application/json";
            context.Response.StatusCode = (int)statusCode;

            var response = Resource<object>.WithMeta(
                new MetaResource((int)errorCode, exception.Message)
            );

            var options = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };
            
            await context.Response.WriteAsync(JsonSerializer.Serialize(response, options));
        }

        private async Task HandleExceptionAsync(HttpContext context, Exception exception)
        {
            _logger.LogError(exception, "Unexpected error");
            
            ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
            var statusCode = errorCode.GetHttpStatusCode();
            
            context.Response.ContentType = "application/json";
            context.Response.StatusCode = (int)statusCode;

            var response = Resource<object>.WithMeta(
                new MetaResource((int)errorCode, errorCode.GetMessage())
            );

            var options = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };
            
            await context.Response.WriteAsync(JsonSerializer.Serialize(response, options));
        }
    }
}