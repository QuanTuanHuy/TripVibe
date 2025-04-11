using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Dto.Response;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Port;
using LocationService.Kernel.Utils;

namespace LocationService.Core.UseCase
{
    public interface IGetTrendingPlaceUseCase
    {
        Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlacesByType(string type, int page, int pageSize);
        Task<TrendingPlaceEntity> GetTrendingPlaceById(long id);
        Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlaces(int page, int pageSize);
    }

    public class GetTrendingPlaceUseCase : IGetTrendingPlaceUseCase 
    {
        private readonly ITrendingPlacePort _trendingPlacePort;
        private readonly ICachePort _cachePort;

        public GetTrendingPlaceUseCase(ITrendingPlacePort trendingPlacePort, ICachePort cachePort)
        {
            _trendingPlacePort = trendingPlacePort;
            _cachePort = cachePort;
        }

        public async Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlacesByType(string type, int page, int pageSize) 
        {
            if (string.IsNullOrEmpty(type) || pageSize <= 0 || page < 0) 
            {
                throw new AppException(ErrorCode.INVALID_PARAMETER);
            }

            string cacheKey = CacheUtils.BuildCacheKeyGetTrendingPlacesByType(type, page, pageSize);
            var cacheResult = await _cachePort.GetFromCacheAsync<PagedResponse<TrendingPlaceEntity>>(cacheKey);
            if (cacheResult != null) 
            {
                return cacheResult;
            }

            var (trendingPlaces, total) = await _trendingPlacePort.GetTrendingPlacesByType(type, page, pageSize);
            if (trendingPlaces == null || !trendingPlaces.Any()) 
            {
                return null;
            }

            var pageInfo = PageInfo.ToPageInfo(page, pageSize, (int)total);
            var result = new PagedResponse<TrendingPlaceEntity>
            {
                PageInfo = pageInfo,
                Data = trendingPlaces
            };

            await _cachePort.SetToCacheAsync(cacheKey, result, CacheConstant.DEFAULT_TTL);
            return result;
        }

        public async Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlaces(int page, int pageSize) 
        {
            if (pageSize <= 0 || page < 0) 
            {
                throw new AppException(ErrorCode.INVALID_PARAMETER);
            }

            string cacheKey = CacheUtils.BuildCacheKeyGetTrendingPlaces(page, pageSize);
            var cacheResult = await _cachePort.GetFromCacheAsync<PagedResponse<TrendingPlaceEntity>>(cacheKey);
            if (cacheResult != null) 
            {
                return cacheResult;
            }

            var (trendingPlaces, total) = await _trendingPlacePort.GetTrendingPlacesAsync(pageSize, page);
            if (trendingPlaces == null || !trendingPlaces.Any()) 
            {
                return null;
            }

            var pageInfo = PageInfo.ToPageInfo(page, pageSize, (int)total);
            var result = new PagedResponse<TrendingPlaceEntity>
            {
                PageInfo = pageInfo,
                Data = trendingPlaces
            };

            await _cachePort.SetToCacheAsync(cacheKey, result, CacheConstant.DEFAULT_TTL);
            return result;
        }

        public async Task<TrendingPlaceEntity> GetTrendingPlaceById(long id) {
            string cacheKey = CacheUtils.BuildCacheKeyGetTrendingPlaceById(id);
            var cacheResult = await _cachePort.GetFromCacheAsync<TrendingPlaceEntity>(cacheKey);
            if (cacheResult != null) {
                return cacheResult;
            }

            var trendingPlace = await _trendingPlacePort.GetTrendingPlaceById(id);
            if (trendingPlace == null) {
                throw new AppException(ErrorCode.TRENDING_PLACE_NOT_FOUND);
            }

            await _cachePort.SetToCacheAsync(cacheKey, trendingPlace, CacheConstant.DEFAULT_TTL);
            return trendingPlace;
        }

    }
}