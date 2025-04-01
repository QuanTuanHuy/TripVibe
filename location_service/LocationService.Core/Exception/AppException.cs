namespace LocationService.Core.Exception
{
    using System;
    using LocationService.Core.Domain.Constant;

    [Serializable]
    public class AppException : Exception
    {
        public ErrorCode ErrorCode { get; }

        public AppException(ErrorCode errorCode)
            : base(errorCode.GetMessage())
        {
            ErrorCode = errorCode;
        }

        public AppException(ErrorCode errorCode, string message)
            : base(message)
        {
            ErrorCode = errorCode;
        }

        public AppException(ErrorCode errorCode, string message, Exception innerException)
            : base(message, innerException)
        {
            ErrorCode = errorCode;
        }

        // Constructor needed for serialization
        protected AppException(System.Runtime.Serialization.SerializationInfo info,
                             System.Runtime.Serialization.StreamingContext context)
            : base(info, context)
        {
        }
    }
}