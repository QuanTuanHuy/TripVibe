namespace PromotionService.Kernel.Utils
{
    public static class DateTimeUtils
    {
        public static DateTime ConvertToDateTime(long timestamp)
        {
            return DateTimeOffset.FromUnixTimeMilliseconds(timestamp).DateTime;
        }

        public static long ConvertToTimestamp(DateTime dateTime)
        {
            return new DateTimeOffset(dateTime).ToUnixTimeMilliseconds();
        }
    }
}