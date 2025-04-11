using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface ITrendingPlacePort
    {
        Task<TrendingPlaceEntity> CreateTrendingPlace(TrendingPlaceEntity trendingPlace);
        Task<TrendingPlaceEntity> GetByReferenceIdAndType(long referenceId, string type);
        Task<(List<TrendingPlaceEntity>, long)> GetTrendingPlacesByType(string type, int page, int size);
        Task<(List<TrendingPlaceEntity>, long)> GetTrendingPlacesAsync(int pageSize, int offset);
        Task<TrendingPlaceEntity> GetTrendingPlaceById(long id);
    }
}