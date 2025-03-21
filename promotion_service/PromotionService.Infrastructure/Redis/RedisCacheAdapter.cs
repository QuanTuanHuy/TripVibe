using Microsoft.Extensions.Logging;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;
using StackExchange.Redis;

namespace PromotionService.Infrastructure.Redis
{
    public class RedisCacheAdapter : ICachePort
    {
        private readonly IConnectionMultiplexer _redisConnection;
        private readonly IDatabase _redisDb;
        private readonly JsonUtils _jsonUtils;
        private readonly ILogger<RedisCacheAdapter> _logger;

        public RedisCacheAdapter(
            IConnectionMultiplexer redisConnection,
            JsonUtils jsonUtils,
            ILogger<RedisCacheAdapter> logger)
        {
            _redisConnection = redisConnection;
            _redisDb = redisConnection.GetDatabase();
            _jsonUtils = jsonUtils;
            _logger = logger;
        }

        public async Task SetToCacheAsync<T>(string key, T value, long expiry)
        {
            try
            {
                string valueStr = _jsonUtils.ToJson(value);
                _logger.LogInformation("Setting cache value for key {Key}: {Value}", key, valueStr);
                await _redisDb.StringSetAsync(key, valueStr, TimeSpan.FromSeconds(expiry));
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error when setting to cache for key {Key}", key);
            }
        }

        public async Task<string> GetFromCacheAsync(string key)
        {
            try
            {
                RedisValue result = await _redisDb.StringGetAsync(key);
                return result.HasValue ? result.ToString() : null;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error when getting from cache for key {Key}", key);
                return null;
            }
        }

        public async Task<T?> GetFromCacheAsync<T>(string key) where T : class
        {
            try
            {
                string valueStr = await GetFromCacheAsync(key);
                if (string.IsNullOrEmpty(valueStr))
                    return null;

                return _jsonUtils.FromJson<T>(valueStr);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error when deserializing from cache for key {Key}", key);
                return null;
            }
        }

        public async Task DeleteFromCacheAsync(string key)
        {
            try
            {
                await _redisDb.KeyDeleteAsync(key);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error when deleting from cache for key {Key}", key);
            }
        }
    }
}