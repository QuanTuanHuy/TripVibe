namespace PromotionService.Core.Port
{
    public interface ICachePort
    {
        Task SetToCacheAsync<T>(string key, T value, TimeSpan? expiry);
        Task<string> GetFromCacheAsync(string key);
        Task<T?> GetFromCacheAsync<T>(string key) where T : class;
        Task DeleteFromCacheAsync(string key);
    }
}